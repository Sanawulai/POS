package com.example.pos.service.impl;

import com.example.pos.configuration.JwtProvider;
import com.example.pos.exceptions.UserException;
import com.example.pos.model.User;
import com.example.pos.repo.UserRepository;
import com.example.pos.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;  // ← IMPORTANT: Import this! Optional is like a "safe box" for values
import java.util.UUID;

// @Service tells Spring: "This is a service class with business logic"
// Services contain the "brain" of your application - the actual work happens here
@Service
public class UserServiceImpl implements UserService {

    // These are the tools this service needs to do its job
    private final UserRepository userRepository;  // Talks to the database
    private final JwtProvider jwtProvider;        // Works with JWT tokens (login tickets)

    // Constructor - Spring automatically gives us these tools
    // This is called "Dependency Injection"
    public UserServiceImpl(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    // ============================================
    // GET USER FROM JWT TOKEN
    // ============================================
    // This method takes a JWT token (like a login ticket) and finds the user
    // Example: Frontend sends "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." token
    //          We decode it and find which user it belongs to
    @Override
    public User getUserFromJwtToken(String token) throws UserException {

        // Step 1: Extract the email from the JWT token
        // The token is encoded and contains the user's email hidden inside
        // jwtProvider decodes it and gives us the email
        String email = jwtProvider.getEmailFormToken(token);

        // Step 2: Find the user by email in the database
        // IMPORTANT: findByEmail returns Optional<User> (not just User)
        // Think of Optional like a "gift box" - it might contain a User, or be empty
        Optional<User> userOptional = userRepository.findByEmail(email);

        // Step 3: Check if the box has something inside
        // .isPresent() asks: "Is there a user in the box?"
        if (!userOptional.isPresent()) {
            // If box is empty (no user found)
            // This means the token is invalid or the user was deleted
            throw new UserException("Invalid token");
        }

        // Step 4: Open the box and get the user
        // .get() means "give me what's inside the box"
        return userOptional.get();
    }

    // ============================================
    // GET CURRENT USER
    // ============================================
    // This gets the currently logged-in user
    // (Note: This implementation might need improvement in production)
    @Override
    public User getCurrentUser() throws UserException {

        // Get email from token
        String email = jwtProvider.getEmailFormToken(jwtProvider.generateToken(null));

        // Find user by email
        // Remember: findByEmail returns Optional<User> (the "safe box")
        Optional<User> userOptional = userRepository.findByEmail(email);

        // Check if user exists (is there something in the box?)
        if (!userOptional.isPresent()) {
            // Box is empty - no user found
            throw new UserException("User not found");
        }

        // Open the box and return the user
        return userOptional.get();
    }

    // ============================================
    // GET USER BY EMAIL
    // ============================================
    // Find a user by their email address
    // Example: getUserByEmail("john@example.com")
    @Override
    public User getUserByEmail(String email) throws UserException {

        // Step 1: Search for user in database by email
        // findByEmail returns Optional<User> (the "safe box")
        Optional<User> userOptional = userRepository.findByEmail(email);

        // Step 2: Check if user exists
        // .isPresent() asks: "Did we find a user?"
        if (!userOptional.isPresent()) {
            // No user found with this email
            throw new UserException("User not found");
        }

        // Step 3: Get the user from the Optional box
        // .get() extracts the User from the Optional
        return userOptional.get();
    }

    // ============================================
    // GET USER BY ID
    // ============================================
    // Find a user by their unique ID (UUID)
    // Example: getUserById("123e4567-e89b-12d3-a456-426614174000")
    @Override
    public User getUserById(UUID id) throws UserException {

        // Step 1: Search for user in database by ID
        // findById also returns Optional<User>
        Optional<User> userOptional = userRepository.findById(id);

        // Step 2: Check if user exists
        if (!userOptional.isPresent()) {
            // No user found with this ID
            throw new UserException("User not found");
        }

        // Step 3: Get the user from the Optional box and return it
        return userOptional.get();
    }

    // ============================================
    // GET ALL USERS
    // ============================================
    // Get every single user from the database
    // Returns a List of User objects
    // Example: [User1, User2, User3, ...]
    @Override
    public List<User> getAllUsers() {

        // findAll() gets ALL users from the users table
        // It returns a List<User>, NOT Optional
        // Why? Because even if there are 0 users, it returns an empty list []
        // So we don't need to check for null or use Optional here
        return userRepository.findAll();
    }
}
