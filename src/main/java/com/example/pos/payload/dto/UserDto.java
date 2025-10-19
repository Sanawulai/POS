package com.example.pos.payload.dto;

import com.example.pos.domain.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {

    private long id;

    private String fullname;

    private String email;

    private String phone;

    private UserRole role;

    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastLogin;
}
