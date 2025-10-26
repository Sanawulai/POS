package com.example.pos.repo;


import com.example.pos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    // This says: "Find me a user with this email"
    // If found, return the User

    //check if email already exists
    Boolean existsByEmail(String email);
}
