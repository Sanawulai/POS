package com.example.pos.controller;

import com.example.pos.configuration.JwtProvider;
import com.example.pos.exceptions.UserException;
import com.example.pos.mapper.UserMapper;
import com.example.pos.model.User;
import com.example.pos.payload.dto.UserDto;
import com.example.pos.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    public UserController(UserService userService,JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile(
            @RequestHeader("Authorization") String jwt) throws UserException {
        User user = userService.getUserFromJwtToken(jwt);
        return ResponseEntity.ok(UserMapper.toDto(user));

    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @RequestHeader("Authorization") String jwt, @PathVariable UUID id) throws UserException {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserMapper.toDto(user));

    }
}
