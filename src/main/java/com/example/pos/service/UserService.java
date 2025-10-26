package com.example.pos.service;

import com.example.pos.exceptions.UserException;
import com.example.pos.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User getUserFromJwtToken(String token) throws UserException;
    User getCurrentUser() throws UserException;
    User getUserByEmail(String email) throws UserException;
    User getUserById(UUID id) throws UserException;
    List<User> getAllUsers();
}
