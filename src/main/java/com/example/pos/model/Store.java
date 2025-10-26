package com.example.pos.model;

import com.example.pos.domain.StoreStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

// @Entity tells Spring: "This is a database table!"
@Entity
@Table(name = "stores")  // Table name in MySQL
public class Store {

    // Primary Key - unique ID for each store
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // Store brand/name (required)
    // Example: "Shoprite Accra Mall", "Game Osu", etc.
    @Column(nullable = false)
    @NotBlank(message = "Store brand/name is required")
    private String brand;

    // Store Admin - ONE user manages this store
    // @OneToOne means: One store has ONE admin, one admin manages ONE store
    @OneToOne(fetch = FetchType.LAZY)  // LAZY = don't load admin until we need it
    @JoinColumn(name = "store_admin_id")  // Creates column "store_admin_id" in stores table
    private User storeAdmin;  // Fixed: should be lowercase "storeAdmin" (Java naming convention)

    // When was this store created?
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // When was it last updated?
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Store description (optional)
    // Example: "Main branch located in Accra Mall"
    @Column(length = 1000)
    private String description;

    // Type of store (optional)
    // Example: "Retail", "Wholesale", "Restaurant", "Supermarket"
    @Column(name = "store_type")
    private String storeType;

    // Store status - is it ACTIVE, INACTIVE, CLOSED?
    @Enumerated(EnumType.STRING)  // Store as text (not numbers)
    @Column(nullable = false)
    private StoreStatus status = StoreStatus.ACTIVE;  // Default to ACTIVE

    // Store contact information (phone, email, address)
    // @Embedded means: These fields are part of Store table, not a separate table
    // Think of it like: Instead of creating a separate "store_contacts" table,
    // we just add phone, email, address columns directly in the stores table
    @Embedded
    private StoreContact contact = new StoreContact();

    // Automatically set timestamps
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = StoreStatus.ACTIVE;  // Default to ACTIVE
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ============================================
    // CONSTRUCTORS
    // ============================================

    // Empty constructor (required by JPA)
    public Store() {
    }

    // Constructor for creating stores easily
    public Store(String brand, User storeAdmin, String description, String storeType) {
        this.brand = brand;
        this.storeAdmin = storeAdmin;
        this.description = description;
        this.storeType = storeType;
        this.status = StoreStatus.ACTIVE;
        this.contact = new StoreContact();
    }

    // ============================================
    // GETTERS AND SETTERS
    // ============================================

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public User getStoreAdmin() {
        return storeAdmin;
    }

    public void setStoreAdmin(User storeAdmin) {
        this.storeAdmin = storeAdmin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public StoreStatus getStatus() {
        return status;
    }

    public void setStatus(StoreStatus status) {
        this.status = status;
    }

    public StoreContact getContact() {
        return contact;
    }

    public void setContact(StoreContact contact) {
        this.contact = contact;
    }
}
