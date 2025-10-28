package com.example.pos.controller;

import com.example.pos.domain.StoreStatus;
import com.example.pos.exceptions.UserException;
import com.example.pos.payload.dto.StoreDto;
import com.example.pos.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// @RestController tells Spring: "This handles HTTP requests and returns JSON"
// Controller = the "waiter" - takes orders (requests) and delivers food (responses)
@RestController
@RequestMapping("/api/stores")  // All endpoints start with /api/stores
public class StoreController {

    // Service that does the actual work
    @Autowired
    private StoreService storeService;

    // ============================================
    // CREATE STORE
    // ============================================
    // POST http://localhost:8080/api/stores?adminId=xxx
    // Request Body: { "brand": "Shoprite Accra", "storeType": "Retail", ... }
    // Only ADMIN can create stores
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")  // Security: Only ADMIN role can access this
    public ResponseEntity<StoreDto> createStore(
            @Valid @RequestBody StoreDto storeDto,  // @Valid checks validation rules (like @NotBlank)
            @RequestParam UUID adminId              // Get adminId from URL parameter (?adminId=xxx)
    ) {
        try {
            // Call service to create the store
            StoreDto createdStore = storeService.createStore(storeDto, adminId);

            // Return 201 CREATED status with the created store
            // ResponseEntity lets us control the HTTP status code
            return new ResponseEntity<>(createdStore, HttpStatus.CREATED);

        } catch (UserException e) {
            // If something goes wrong, return 400 BAD REQUEST
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // ============================================
    // GET ALL STORES
    // ============================================
    // GET http://localhost:8080/api/stores
    // Returns a list of all stores
    // ADMIN and MANAGER can access this
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<StoreDto>> getAllStores() {

        // Get all stores from service
        List<StoreDto> stores = storeService.getAllStores();

        // Return 200 OK with the list
        return new ResponseEntity<>(stores, HttpStatus.OK);
    }

    // ============================================
    // GET STORE BY ID
    // ============================================
    // GET http://localhost:8080/api/stores/123e4567-e89b-12d3-a456-426614174000
    // Returns a specific store by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable UUID id) {
        // @PathVariable extracts {id} from the URL
        // Example: /api/stores/123abc → id = "123abc"

        try {
            StoreDto store = storeService.getStoreById(id);
            return new ResponseEntity<>(store, HttpStatus.OK);

        } catch (UserException e) {
            // Store not found - return 404
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // ============================================
    // GET STORES BY STATUS
    // ============================================
    // GET http://localhost:8080/api/stores/status/ACTIVE
    // Returns all stores with a specific status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<List<StoreDto>> getStoresByStatus(@PathVariable StoreStatus status) {

        List<StoreDto> stores = storeService.getStoresByStatus(status);
        return new ResponseEntity<>(stores, HttpStatus.OK);
    }

    // ============================================
    // UPDATE STORE
    // ============================================
    // PUT http://localhost:8080/api/stores/123e4567-e89b-12d3-a456-426614174000
    // Request Body: { "brand": "Updated Name", ... }
    // Only ADMIN can update stores
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StoreDto> updateStore(
            @PathVariable UUID id,
            @Valid @RequestBody StoreDto storeDto
    ) {
        try {
            StoreDto updatedStore = storeService.updateStore(id, storeDto);
            return new ResponseEntity<>(updatedStore, HttpStatus.OK);

        } catch (UserException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // ============================================
    // DELETE STORE
    // ============================================
    // DELETE http://localhost:8080/api/stores/123e4567-e89b-12d3-a456-426614174000
    // Only ADMIN can delete stores
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteStore(@PathVariable UUID id) {

        try {
            storeService.deleteStore(id);

            // Return 200 OK with success message
            return new ResponseEntity<>("Store deleted successfully", HttpStatus.OK);

        } catch (UserException e) {
            return new ResponseEntity<>("Store not found", HttpStatus.NOT_FOUND);
        }
    }

    // ============================================
    // CHANGE STORE STATUS
    // ============================================
    // PATCH http://localhost:8080/api/stores/123/status?status=BLOCKED
    // Change a store's status (ACTIVE → BLOCKED, etc.)
    // Only ADMIN can change status
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StoreDto> changeStoreStatus(
            @PathVariable UUID id,
            @RequestParam StoreStatus status  // Get status from URL parameter (?status=BLOCKED)
    ) {
        try {
            StoreDto updatedStore = storeService.changeStoreStatus(id, status);
            return new ResponseEntity<>(updatedStore, HttpStatus.OK);

        } catch (UserException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}