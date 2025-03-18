package com.douglasmarq.auth.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.douglasmarq.auth.domain.dto.RegisterUserRequest;
import com.douglasmarq.auth.domain.exception.RegisterApiException;
import com.douglasmarq.auth.domain.repository.UserRepository;
import com.douglasmarq.auth.infraestructure.logs.AnonymizeLogger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class RegisterServiceTest {

    @Mock private UserRepository userRepository;

    @Mock private EmailService emailService;

    @Mock private AnonymizeLogger logger;

    @Mock private PasswordService passwordService;

    @InjectMocks private RegisterService registerService;

    private RegisterUserRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new RegisterUserRequest("Test User", "test@example.com", "123", "123");
    }

    @Test
    @DisplayName("Should register user successfully when all data is valid")
    void shouldRegisterUserSuccessfully() {
        registerService.register(validRequest);

        verify(userRepository, times(1)).save(any());
        verify(emailService, times(1)).sendWelcomeEmail(validRequest.getEmail());
    }

    @Test
    @DisplayName("Should throw RegisterApiException when passwords don't match")
    void shouldThrowExceptionWhenPasswordsDoNotMatch() {
        RegisterUserRequest requestWithMismatchedPasswords =
                new RegisterUserRequest("Test User", "test@example.com", "123", "1234");

        RegisterApiException exception =
                assertThrows(
                        RegisterApiException.class,
                        () -> registerService.register(requestWithMismatchedPasswords));

        assertEquals("password does not match", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendWelcomeEmail(any());
    }

    @Test
    @DisplayName("Should throw RegisterApiException when email is already registered")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException("Duplicate email"));

        RegisterApiException exception =
                assertThrows(
                        RegisterApiException.class, () -> registerService.register(validRequest));

        assertEquals("e-mail already registered", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
        verify(emailService, never()).sendWelcomeEmail(any());
    }

    @Test
    @DisplayName("Should throw RegisterApiException when an unexpected error occurs")
    void shouldThrowExceptionWhenUnexpectedErrorOccurs() {
        String errorMessage = "Unexpected error";
        when(userRepository.save(any())).thenThrow(new RuntimeException(errorMessage));

        RegisterApiException exception =
                assertThrows(
                        RegisterApiException.class, () -> registerService.register(validRequest));

        assertEquals(errorMessage, exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
        verify(emailService, never()).sendWelcomeEmail(any());
    }
}
