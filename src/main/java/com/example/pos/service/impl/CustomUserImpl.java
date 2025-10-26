package com.example.pos.service.impl;

import com.example.pos.repo.UserRepository;
import com.example.pos.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;  // ← ADD THIS IMPORT!

// @Service tells Spring: "This is a service that loads user details for authentication"
@Service
public class CustomUserImpl implements UserDetailsService {

    // This is the tool we use to talk to the database
    @Autowired
    private UserRepository userRepository;

    // Constructor - Spring gives us the userRepository automatically
    public CustomUserImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ============================================
    // LOAD USER BY USERNAME (actually by email in our case)
    // ============================================
    // This method is called by Spring Security when someone tries to login
    // It finds the user and returns their details (email, password, role)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Step 1: Find user by email (username is actually email in our POS system)
        // Remember: findByEmail returns Optional<User> (a "box" that might be empty)
        Optional<User> userOptional = userRepository.findByEmail(username);

        // Step 2: Check if user exists
        // .isPresent() asks: "Is there a user in the box?"
        if (!userOptional.isPresent()) {
            // If box is empty (no user found), throw an error
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        // Step 3: Get the user out of the Optional box
        // .get() means "open the box and give me what's inside"
        User user = userOptional.get();

        // Step 4: Create the user's authority (permission/role)
        // GrantedAuthority is Spring Security's way of saying "what can this user do?"
        // Example: user.getRole() might be "ROLE_CASHIER"
        GrantedAuthority authority = new SimpleGrantedAuthority(
                user.getRole().toString()  // Convert enum to String (ROLE_CASHIER, ROLE_ADMIN, etc.)
        );

        // Step 5: Put the authority in a collection
        // Collections.singleton() creates a list with just ONE item
        // Why? Because Spring Security expects a Collection, not just one authority
        Collection<GrantedAuthority> authorities = Collections.singleton(authority);

        // Step 6: Return Spring Security's UserDetails object
        // This is what Spring Security uses to check login and permissions
        // It contains: email, password, and roles
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),      // Username (we use email as username)
                user.getPassword(),   // Encrypted password
                authorities           // Roles/permissions (CASHIER, ADMIN, etc.)
        );
    }
}
