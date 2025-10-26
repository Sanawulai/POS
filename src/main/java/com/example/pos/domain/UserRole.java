package com.example.pos.domain;

public enum UserRole {
    ROLE_ADMIN,           // The boss - full access
    ROLE_USER,            // Regular user (maybe customer?)
    ROLE_CASHIER,         // Person at the counter selling items
    ROLE_BRANCH_MANAGER,  // Manages one branch/location
    ROLE_STORE_MANAGER,   // Manages the whole store
}