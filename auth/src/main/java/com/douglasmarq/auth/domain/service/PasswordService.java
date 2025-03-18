package com.douglasmarq.auth.domain.service;

import lombok.AllArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PasswordService {

    private final BCryptPasswordEncoder passwordEncoder;

    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean decode(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }
}
