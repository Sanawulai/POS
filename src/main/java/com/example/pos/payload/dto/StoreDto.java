package com.example.pos.payload.dto;

import com.example.pos.domain.StoreStatus;
import com.example.pos.model.StoreContact;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

// DTO = Data Transfer Object
// This is what we send between frontend and backend
// Think of it like a "package" for shipping data

// @Data creates getters, setters, toString automatically (Lombok magic!)
@Data
// @NoArgsConstructor creates empty constructor: new StoreDto()
@NoArgsConstructor
// @AllArgsConstructor creates constructor with all fields
@AllArgsConstructor
public class StoreDto {

    // Store ID (only set when returning data, not when creating)
    private UUID id;

    // Store brand/name (required!)
    // @NotBlank means: cannot be null, empty, or just whitespace
    @NotBlank(message = "Store brand/name is required")
    private String brand;

    // Store admin's ID (we send just the ID, not the whole User object)
    // Why? To keep the data lightweight and avoid circular references
    private UUID storeAdminId;

    // Store admin's full name (useful for displaying in frontend)
    // Example: "John Doe" instead of just showing the UUID
    private String storeAdminName;

    // Store description (optional)
    // Example: "Main branch located in Accra Mall, 2nd floor"
    private String description;

    // Store type (optional)
    // Example: "Retail", "Wholesale", "Restaurant", "Supermarket"
    private String storeType;

    // Store status - is it ACTIVE, PENDING, or BLOCKED?
    private StoreStatus status;

    // Store contact information (phone, email, address)
    // This is embedded, so it's part of the same object
    private StoreContact contact;

    // Timestamps - when the store was created and last updated
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
