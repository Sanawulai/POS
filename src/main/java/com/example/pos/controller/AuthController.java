package com.example.pos.controller;

import com.example.pos.exceptions.UserException;
import com.example.pos.payload.dto.UserDto;
import com.example.pos.payload.response.AuthResponse;
import com.example.pos.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUpHandler(@RequestBody UserDto userDto) throws Exception, UserException {
        return ResponseEntity.ok(authService.signup(userDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> LoginHandler(@RequestBody UserDto userDto) throws Exception, UserException {
        return ResponseEntity.ok(authService.login(userDto));
    }
}
