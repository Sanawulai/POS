package com.example.pos.domain;

// StoreStatus - Represents the current status of a store
// This helps manage which stores are operational
public enum StoreStatus {

    ACTIVE,    // Store is open and operating normally
    // Cashiers can make sales, everything works

    PENDING,   // Store is being set up, not yet ready
    // Maybe waiting for approval or configuration

    BLOCKED    // Store is blocked/suspended
    // Cannot operate - maybe due to payment issues or violations
}