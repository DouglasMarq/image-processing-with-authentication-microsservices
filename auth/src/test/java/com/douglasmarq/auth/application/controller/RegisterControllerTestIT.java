package com.douglasmarq.auth.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.douglasmarq.auth.domain.PlanType;
import com.douglasmarq.auth.domain.Users;
import com.douglasmarq.auth.domain.dto.RegisterUserRequest;
import com.douglasmarq.auth.domain.exception.RegisterApiException;
import com.douglasmarq.auth.domain.repository.UserRepository;
import com.douglasmarq.auth.domain.service.EmailService;
import com.douglasmarq.auth.domain.service.PasswordService;
import com.douglasmarq.auth.domain.service.RegisterService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class RegisterControllerTestIT {
    @Mock private UserRepository userRepository;

    @Mock private EmailService emailService;

    @Mock private PasswordService passwordService;

    private RegisterService registerService;

    private RegisterController registerController;

    @BeforeEach
    void setUp() {
        if (registerService == null) {
            registerService = buildRegisterService();
        }
        if (registerController == null) {
            registerController = buildRegisterController(registerService);
        }
        Mockito.reset(userRepository);
        Mockito.reset(emailService);
    }

    private RegisterService buildRegisterService() {
        return new RegisterService(userRepository, emailService, passwordService);
    }

    private RegisterController buildRegisterController(RegisterService registerService) {
        return new RegisterController(registerService);
    }

    private Users createValidUser() {
        return Users.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .plan(PlanType.BASIC)
                .quotas(10)
                .build();
    }

    private RegisterUserRequest createValidRequest() {
        return new RegisterUserRequest(
                "Test User", "test@example.com", "password123", "password123");
    }

    @Test
    @DisplayName("Should register a new user")
    void shouldRegisterUser() {
        RegisterUserRequest request = createValidRequest();

        when(userRepository.save(any(Users.class))).thenAnswer(i -> i.getArguments()[0]);
        doNothing().when(emailService).sendWelcomeEmail(any());

        ResponseEntity response = registerController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userRepository).save(any(Users.class));
        verify(emailService).sendWelcomeEmail(any());
    }

    @Test
    @DisplayName("Should throw when user already exists")
    void shouldThrowWhenUserExists() {
        RegisterUserRequest request = createValidRequest();
        Users existingUser = createValidUser();

        when(passwordService.encode(existingUser.getPassword())).thenReturn(existingUser.getPassword());

        when(userRepository.save(existingUser))
                .thenThrow(new DataIntegrityViolationException("User already exists"));

        try {
            registerController.register(request);
        } catch (RegisterApiException e) {
            assertEquals("e-mail already registered", e.getMessage());
        }

        verify(userRepository).save(existingUser);
        verify(emailService, never()).sendWelcomeEmail(any());
    }
}
