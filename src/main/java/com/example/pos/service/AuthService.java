package com.example.pos.service;

import com.example.pos.exceptions.UserException;
import com.example.pos.payload.dto.UserDto;
import com.example.pos.payload.response.AuthResponse;

public interface AuthService {

    AuthResponse signup(UserDto userDto) throws UserException;
    AuthResponse login(UserDto userDto) throws UserException;
}
