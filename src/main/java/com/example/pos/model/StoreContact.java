package com.example.pos.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import lombok.Data;

// @Data is Lombok magic! It automatically creates:
// - Getters for all fields
// - Setters for all fields
// - toString() method
// - equals() and hashCode() methods
// This saves you from writing tons of boilerplate code!
@Data

// @Embeddable means: This is NOT a separate table in the database
// These fields (address, phone, email) will be added as columns
// directly in the "stores" table
@Embeddable
public class StoreContact {

    // Store's physical address
    // Example: "123 Oxford Street, Osu, Accra"
    private String address;

    // Store's phone number
    // Example: "+233 24 123 4567"
    private String phone;

    // Store's email address
    // @Email validates that this is a proper email format
    @Email(message = "Email should be valid")
    private String email;
}