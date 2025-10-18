package com.example.pos.service.impl;

import com.example.pos.repo.UserRepository;
import com.example.pos.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    public CustomUserImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        GrantedAuthority authority = new SimpleGrantedAuthority(
                user.getRole().toString()
        );
        Collection<GrantedAuthority> authorities =
                Collections.singleton(authority);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),user.getPassword(),authorities
                
        );
    }
}
