package com.example.pos.service.impl;

import com.example.pos.configuration.JwtProvider;
import com.example.pos.domain.UserRole;
import com.example.pos.exceptions.UserException;
import com.example.pos.mapper.UserMapper;
import com.example.pos.model.User;
import com.example.pos.payload.dto.UserDto;
import com.example.pos.payload.response.AuthResponse;
import com.example.pos.repo.UserRepository;
import com.example.pos.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserImpl customUserImpl;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtProvider jwtProvider,CustomUserImpl customUserImpl) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.customUserImpl = customUserImpl;

    }
    @Override
    public AuthResponse signup(UserDto userDto) throws UserException {
        User user = userRepository.findByEmail(userDto.getEmail());
        if (user != null) {
            throw new UserException("email id already regisitered ! please try again with a different email");
        }
        if (userDto.getRole().equals(UserRole.ROLE_ADMIN)){
            throw new UserException("role admin is not allowed!)");
        }
        User newUser = new User();
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        newUser.setRole(userDto.getRole());
        newUser.setFullname(userDto.getFullname());
        newUser.setPhone(userDto.getPhone());
        newUser.setLastLogin(userDto.getLastLogin());
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(newUser);



        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDto.getEmail(), userDto.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Registered Successfully");
        authResponse.setUser(UserMapper.toDto(savedUser));
        return authResponse;
    }

    @Override
    public AuthResponse login(UserDto userDto) throws UserException {
        String email = userDto.getEmail();
        String password = userDto.getPassword();
        Authentication authentication = authenticate(email,password);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String role = authorities.iterator().next().getAuthority();

        String jwt = jwtProvider.generateToken(authentication);

        User user = userRepository.findByEmail(email);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Login Successfully");
        authResponse.setUser(UserMapper.toDto(user));


        return authResponse;
    }

    private Authentication authenticate(String email, String password) throws UserException {

        UserDetails userDetails = customUserImpl.loadUserByUsername(email);
        if(userDetails == null){
            throw new UserException("email id doesnt exist "+email  );
        }
        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new UserException("password doesnt match");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }
}
