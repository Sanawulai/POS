package com.example.pos.service.impl;

import com.example.pos.configuration.JwtProvider;
import com.example.pos.domain.UserRole;
import com.example.pos.exceptions.UserException;
import com.example.pos.mapper.UserMapper;
import com.example.pos.model.User;
import com.example.pos.payload.dto.UserDto;
import com.example.pos.payload.response.AuthResponse;
import com.example.pos.repo.UserRepository;
import com.example.pos.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;  // IMPORTANT: Import this! It's like a "safe box" for values that might not exist

// @Service tells Spring: "Hey, this is a service class! Create one instance and manage it for me"
@Service
public class AuthServiceImpl implements AuthService {

    // These are the "tools" this service needs to do its job
    private final UserRepository userRepository;      // Talks to the database
    private final PasswordEncoder passwordEncoder;    // Encrypts passwords (security!)
    private final JwtProvider jwtProvider;            // Creates JWT tokens (like a special ID card)
    private final CustomUserImpl customUserImpl;      // Loads user details for authentication

    // @Autowired means: "Spring, automatically give me these tools when you create this service"
    // This is called "Dependency Injection" - Spring gives us what we need
    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           JwtProvider jwtProvider, CustomUserImpl customUserImpl) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.customUserImpl = customUserImpl;
    }

    // ============================================
    // SIGNUP METHOD - Register a new user
    // ============================================
    @Override
    public AuthResponse signup(UserDto userDto) throws UserException {

        // Step 1: Check if email already exists in database
        // Think of Optional like a "gift box" - it might contain a User, or it might be empty
        Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());

        // .isPresent() asks: "Is there something in the box?"
        // If YES (box has a user) = email already exists!
        if (existingUser.isPresent()) {
            // Stop! Throw an error - can't register with duplicate email
            throw new UserException("Email already registered! Please try a different email");
        }

        // Step 2: Don't allow people to register as ADMIN (security!)
        // Only existing admins should create new admin accounts
        if (userDto.getRole().equals(UserRole.ROLE_ADMIN)) {
            throw new UserException("Role admin is not allowed!");
        }

        // Step 3: Create a brand new User object
        User newUser = new User();

        // Fill in all the user's information
        newUser.setEmail(userDto.getEmail());

        // IMPORTANT: Never save passwords as plain text!
        // passwordEncoder.encode() encrypts the password (turns "password123" into gibberish)
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

        newUser.setRole(userDto.getRole());              // CASHIER, MANAGER, etc.
        newUser.setFullname(userDto.getFullname());      // Person's name
        newUser.setPhone(userDto.getPhone());            // Phone number

        // Set timestamps - record WHEN this user was created
        newUser.setLastLogin(LocalDateTime.now());       // Right now!
        newUser.setCreatedAt(LocalDateTime.now());       // When account was created
        newUser.setUpdatedAt(LocalDateTime.now());       // When account was last modified

        // Step 4: Save the new user to the database
        // userRepository.save() is like pressing "Save" in a document - it stores it permanently
        User savedUser = userRepository.save(newUser);

        // Step 5: Create authentication (prove who they are)
        // This is like showing your ID card to prove you're you
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDto.getEmail(), userDto.getPassword());

        // Put this authentication in the "SecurityContext" (Spring's security system)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Step 6: Generate a JWT token
        // JWT = JSON Web Token (like a special ticket that proves you're logged in)
        // This token will be sent to the frontend and used for future requests
        String jwt = jwtProvider.generateToken(authentication);

        // Step 7: Prepare the response to send back to the frontend
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);                                  // The token
        authResponse.setMessage("Registered Successfully");        // Success message
        authResponse.setUser(UserMapper.toDto(savedUser));         // User info (without password!)

        // Return the response - this goes back to the controller, then to the frontend
        return authResponse;
    }

    // ============================================
    // LOGIN METHOD - Log in an existing user
    // ============================================
    @Override
    public AuthResponse login(UserDto userDto) throws UserException {
        // Get email and password from the login request
        String email = userDto.getEmail();
        String password = userDto.getPassword();

        // Step 1: Authenticate - check if email and password are correct
        // This calls the authenticate() method below (scroll down to see it)
        Authentication authentication = authenticate(email, password);

        // Put authentication in SecurityContext (tell Spring this user is now logged in)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get the user's role (ADMIN, CASHIER, etc.)
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.iterator().next().getAuthority();

        // Step 2: Generate JWT token (login ticket)
        String jwt = jwtProvider.generateToken(authentication);

        // Step 3: Update the user's "last login" time in database
        // First, find the user by email
        Optional<User> userOptional = userRepository.findByEmail(email);

        // Check: Is there a user in the Optional box?
        if (!userOptional.isPresent()) {
            // If box is empty (no user found) = something went wrong!
            throw new UserException("User not found");
        }

        // Get the user out of the Optional box
        // .get() means "open the box and give me what's inside"
        User user = userOptional.get();

        // Update last login time to RIGHT NOW
        user.setLastLogin(LocalDateTime.now());

        // Save the updated user back to database
        userRepository.save(user);

        // Step 4: Prepare response to send back
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);                          // Login token
        authResponse.setMessage("Login Successfully");     // Success message
        authResponse.setUser(UserMapper.toDto(user));      // User info

        return authResponse;
    }

    // ============================================
    // AUTHENTICATE METHOD - Check if email/password are correct
    // ============================================
    // This is a PRIVATE method (only used inside this class)
    private Authentication authenticate(String email, String password) throws UserException {

        // Step 1: Load the user from database by email
        // CustomUserImpl.loadUserByUsername() gets user details for Spring Security
        UserDetails userDetails = customUserImpl.loadUserByUsername(email);

        // Step 2: Check if user exists
        if (userDetails == null) {
            // No user found with this email!
            throw new UserException("Email doesn't exist: " + email);
        }

        // Step 3: Check if password matches
        // passwordEncoder.matches() compares:
        // - "password" = what user typed
        // - userDetails.getPassword() = encrypted password from database
        // It decrypts and compares them
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            // Password is wrong!
            throw new UserException("Password doesn't match");
        }

        // Step 4: Everything is correct! Create and return authentication
        // This proves: "Yes, this person is who they say they are!"
        return new UsernamePasswordAuthenticationToken(
                userDetails,              // The user
                password,                 // Their password
                userDetails.getAuthorities()  // Their permissions (roles)
        );
    }
}