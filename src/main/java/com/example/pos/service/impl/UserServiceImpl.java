package com.example.pos.service.impl;

import com.example.pos.configuration.JwtProvider;
import com.example.pos.exceptions.UserException;
import com.example.pos.model.User;
import com.example.pos.repo.UserRepository;
import com.example.pos.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public UserServiceImpl(UserRepository userRepository,JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }
    @Override
    public User getUserFromJwtToken(String token) throws UserException {
        String email = jwtProvider.getEmailFormToken(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserException("Invalid token");
        }
        // Return the valid user instead of 'null'
        return user;
    }



    @Override
    public User getCurrentUser() throws UserException {
        String email = jwtProvider.getEmailFormToken(jwtProvider.generateToken(null));
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new UserException("User not found");
        }
        return user;

    }

    @Override
    public User getUserByEmail(String email) throws UserException {
        User user = userRepository.findByEmail(email);
        if (user == null){
            throw new UserException("User not found");
        }
        return user;
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(null);

    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
