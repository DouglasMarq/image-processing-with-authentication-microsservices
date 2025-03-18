package com.douglasmarq.auth.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class PasswordServiceTest {

    @Mock private BCryptPasswordEncoder passwordEncoder;

    private PasswordService passwordService;

    private final String rawPassword = "password123";
    private final String encodedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz";

    @BeforeEach
    void setUp() {
        if (passwordService == null) {
            passwordService = buildPasswordService();
        }
    }

    private PasswordService buildPasswordService() {
        return new PasswordService(passwordEncoder);
    }

    @Test
    @DisplayName("Should encode password successfully")
    void shouldEncodePassword() {
        when(passwordEncoder.encode(eq(rawPassword))).thenReturn(encodedPassword);
        String result = passwordService.encode(rawPassword);

        assertEquals(encodedPassword, result);
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    @DisplayName("Should return true when passwords match")
    void shouldReturnTrueWhenPasswordsMatch() {
        when(passwordEncoder.matches(eq(rawPassword), eq(encodedPassword))).thenReturn(true);

        boolean result = passwordService.decode(rawPassword, encodedPassword);

        assertTrue(result);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    @DisplayName("Should return false when passwords don't match")
    void shouldReturnFalseWhenPasswordsDontMatch() {
        String wrongPassword = "wrong123";

        when(passwordEncoder.matches(eq(wrongPassword), eq(encodedPassword))).thenReturn(false);

        boolean result = passwordService.decode(wrongPassword, encodedPassword);

        assertFalse(result);
        verify(passwordEncoder).matches(wrongPassword, encodedPassword);
    }
}
