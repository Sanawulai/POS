package com.example.pos.service;

import com.example.pos.domain.StoreStatus;
import com.example.pos.exceptions.UserException;
import com.example.pos.model.Store;
import com.example.pos.model.User;
import com.example.pos.payload.dto.StoreDto;
import com.example.pos.repo.StoreRepository;
import com.example.pos.repo.UserRepository;
import com.example.pos.mapper.StoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// @Service tells Spring: "This is a service (business logic)"
// Service = the "brain" where the actual work happens
@Service
public class StoreService {

    // Tools we need (Spring gives us these automatically)
    private StoreRepository storeRepository;  // Talks to database
    private UserRepository userRepository;// To find the admin user

    @Autowired
    public StoreService(StoreRepository storeRepository, UserRepository userRepository) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }
    // ============================================
    // CREATE STORE
    // ============================================
    // Creates a new store with an admin
    public StoreDto createStore(StoreDto storeDto, UUID adminId) throws UserException {

        // Step 1: Check if store brand already exists
        // We don't want duplicate store names!
        if (storeRepository.existsByBrand(storeDto.getBrand())) {
            throw new UserException("Store brand already exists!");
        }

        // Step 2: Find the admin user
        // The admin is the person who will manage this store
        Optional<User> adminOptional = userRepository.findById(adminId);
        if (!adminOptional.isPresent()) {
            throw new UserException("Admin user not found!");
        }
        User admin = adminOptional.get();

        // Step 3: Convert DTO to Entity (Store object)
        // Mapper translates from API format to database format
        Store store = StoreMapper.toEntity(storeDto);
        store.setStoreAdmin(admin);

        // If status is not set, default to ACTIVE
        if (store.getStatus() == null) {
            store.setStatus(StoreStatus.ACTIVE);
        }

        // Step 4: Save to database
        // This inserts a new row in the stores table
        Store savedStore = storeRepository.save(store);

        // Step 5: Convert back to DTO and return
        // We send back the DTO (not the entity) to the controller
        return StoreMapper.toDto(savedStore);
    }

    // ============================================
    // GET ALL STORES
    // ============================================
    // Returns a list of all stores in the system
    public List<StoreDto> getAllStores() {

        // Get all stores from database
        List<Store> stores = storeRepository.findAll();

        // Convert each Store to StoreDto
        // .stream() lets us process the list
        // .map() transforms each Store to StoreDto using the mapper
        // .collect() gathers them back into a list
        return stores.stream()
                .map(StoreMapper::toDto)
                .collect(Collectors.toList());
    }

    // ============================================
    // GET STORE BY ID
    // ============================================
    // Find a specific store by its unique ID
    public StoreDto getStoreById(UUID id) throws UserException {

        // Find store by ID
        // Returns Optional<Store> - a "box" that might be empty
        Optional<Store> storeOptional = storeRepository.findById(id);

        // Check if store exists
        if (!storeOptional.isPresent()) {
            throw new UserException("Store not found!");
        }

        // Get the store from the Optional box
        Store store = storeOptional.get();

        // Convert to DTO and return
        return StoreMapper.toDto(store);
    }

    // ============================================
    // GET STORES BY STATUS
    // ============================================
    // Find all stores with a specific status (ACTIVE, PENDING, BLOCKED)
    public List<StoreDto> getStoresByStatus(StoreStatus status) {

        // Find all stores with this status
        List<Store> stores = storeRepository.findByStatus(status);

        // Convert each Store to StoreDto and return
        return stores.stream()
                .map(StoreMapper::toDto)
                .collect(Collectors.toList());
    }

    // ============================================
    // UPDATE STORE
    // ============================================
    // Update an existing store's information
    public StoreDto updateStore(UUID id, StoreDto storeDto) throws UserException {

        // Step 1: Find existing store
        Optional<Store> existingStoreOptional = storeRepository.findById(id);

        if (!existingStoreOptional.isPresent()) {
            throw new UserException("Store not found!");
        }

        Store existingStore = existingStoreOptional.get();

        // Step 2: Update fields
        // We update only the fields that can be changed
        existingStore.setBrand(storeDto.getBrand());
        existingStore.setDescription(storeDto.getDescription());
        existingStore.setStoreType(storeDto.getStoreType());
        existingStore.setStatus(storeDto.getStatus());
        existingStore.setContact(storeDto.getContact());

        // Step 3: Save updated store
        // This updates the row in the database
        Store updatedStore = storeRepository.save(existingStore);

        // Step 4: Convert to DTO and return
        return StoreMapper.toDto(updatedStore);
    }

    // ============================================
    // DELETE STORE
    // ============================================
    // Delete a store from the system
    public void deleteStore(UUID id) throws UserException {

        // Check if store exists
        if (!storeRepository.existsById(id)) {
            throw new UserException("Store not found!");
        }

        // Delete the store
        // This removes the row from the database
        storeRepository.deleteById(id);
    }

    // ============================================
    // CHANGE STORE STATUS
    // ============================================
    // Change a store's status (ACTIVE → BLOCKED, etc.)
    public StoreDto changeStoreStatus(UUID id, StoreStatus newStatus) throws UserException {

        // Find the store
        Optional<Store> storeOptional = storeRepository.findById(id);

        if (!storeOptional.isPresent()) {
            throw new UserException("Store not found!");
        }

        Store store = storeOptional.get();

        // Update the status
        store.setStatus(newStatus);

        // Save and return
        Store updatedStore = storeRepository.save(store);
        return StoreMapper.toDto(updatedStore);
    }
}