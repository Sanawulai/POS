package com.example.pos.repo;

import com.example.pos.domain.StoreStatus;
import com.example.pos.model.Store;
import com.example.pos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// @Repository tells Spring: "This talks to the database for Store table"
// Repository = like a librarian who helps you find and store books (data)
@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

    // Find store by brand name
    // Example: findByBrand("Shoprite Accra Mall")
    // Returns Optional<Store> - a "box" that might contain a store or be empty
    Optional<Store> findByBrand(String brand);

    // Check if a store brand name already exists
    // Example: existsByBrand("Game Osu") → true if exists, false if not
    // Useful when creating new stores to avoid duplicates
    Boolean existsByBrand(String brand);

    // Find store by admin (the user who manages it)
    // Example: Find which store John manages
    Optional<Store> findByStoreAdmin(User storeAdmin);

    // Find all stores by status
    // Example: Get all ACTIVE stores, or all BLOCKED stores
    // Returns a List because multiple stores can have the same status
    List<Store> findByStatus(StoreStatus status);

    // Find stores by type
    // Example: Get all "Retail" stores, or all "Wholesale" stores
    List<Store> findByStoreType(String storeType);
}