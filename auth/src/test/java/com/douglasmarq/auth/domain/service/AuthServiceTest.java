package com.douglasmarq.auth.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.douglasmarq.auth.domain.PlanType;
import com.douglasmarq.auth.domain.Users;
import com.douglasmarq.auth.domain.dto.TokenResponse;
import com.douglasmarq.auth.domain.dto.UserToken;
import com.douglasmarq.auth.domain.repository.UserRepository;
import com.douglasmarq.auth.infraestructure.utils.JwtUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock private UserRepository userRepository;

    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthService authService;

    private final String accessToken = "mock-access-token";
    private final String refreshToken = "mock-refresh-token";

    private Users createValidUser() {
        return Users.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .plan(PlanType.BASIC)
                .quotas(10)
                .build();
    }

    @Test
    @DisplayName("Should Return Tokens when Credentials are Valid")
    void shouldReturnTokensWhenCredentialsAreValid() {
        var validUser = createValidUser();

        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Optional.of(validUser));
        when(jwtUtil.generateAccessToken(
                        validUser.getEmail(), validUser.getId(), validUser.getPlan().toString()))
                .thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(
                        validUser.getEmail(), validUser.getId(), validUser.getPlan().toString()))
                .thenReturn(refreshToken);

        TokenResponse result =
                authService.authenticate(validUser.getEmail(), validUser.getPassword());

        assertNotNull(result);
        assertEquals(accessToken, result.getAccessToken());
        assertEquals(refreshToken, result.getRefreshToken());
        verify(userRepository).findByEmail(validUser.getEmail());
        verify(jwtUtil)
                .generateAccessToken(
                        validUser.getEmail(), validUser.getId(), validUser.getPlan().toString());
        verify(jwtUtil)
                .generateRefreshToken(
                        validUser.getEmail(), validUser.getId(), validUser.getPlan().toString());
    }

    @Test
    @DisplayName("Should Throw Exception when User is Not Found")
    void shouldThrowExceptionWhenUserIsNotFound() {
        var validUser = createValidUser();

        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                authService.authenticate(
                                        validUser.getEmail(), validUser.getPassword()));

        assertEquals("User not found or password is incorrect", exception.getMessage());
        verify(userRepository).findByEmail(validUser.getEmail());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    @DisplayName("Should Throw Exception when Password is Invalid")
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        var validUser = createValidUser();

        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Optional.of(validUser));
        String wrongPassword = "wrongPassword";

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> authService.authenticate(validUser.getEmail(), wrongPassword));
        assertEquals("User not found or password is incorrect", exception.getMessage());
        verify(userRepository).findByEmail(validUser.getEmail());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    @DisplayName("Should Return New Tokens when Refresh Token is Valid")
    void shouldReturnNewTokensWhenRefreshTokenIsValid() {
        var validUser = createValidUser();

        UserToken userToken =
                UserToken.builder()
                        .userId(validUser.getId())
                        .email(validUser.getEmail())
                        .plan(validUser.getPlan().toString())
                        .build();

        when(jwtUtil.validateTokenAndGetData(refreshToken)).thenReturn(userToken);
        when(jwtUtil.generateAccessToken(
                        validUser.getEmail(), validUser.getId(), validUser.getPlan().toString()))
                .thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(
                        validUser.getEmail(), validUser.getId(), validUser.getPlan().toString()))
                .thenReturn("new-refresh-token");

        TokenResponse result = authService.refresh(refreshToken);

        assertNotNull(result);
        assertEquals(accessToken, result.getAccessToken());
        assertEquals("new-refresh-token", result.getRefreshToken());
        verify(jwtUtil).validateTokenAndGetData(refreshToken);
        verify(jwtUtil)
                .generateAccessToken(
                        validUser.getEmail(), validUser.getId(), validUser.getPlan().toString());
        verify(jwtUtil)
                .generateRefreshToken(
                        validUser.getEmail(), validUser.getId(), validUser.getPlan().toString());
    }

    @Test
    @DisplayName("Should Propagate Exception when Token Validation Fails")
    void shouldPropagateExceptionWhenTokenValidationFails() {
        when(jwtUtil.validateTokenAndGetData(anyString()))
                .thenThrow(new RuntimeException("Invalid token"));

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> authService.refresh(refreshToken));
        assertEquals("Invalid token", exception.getMessage());
        verify(jwtUtil).validateTokenAndGetData(refreshToken);
        verify(jwtUtil, never()).generateAccessToken(anyString(), any(), anyString());
        verify(jwtUtil, never()).generateRefreshToken(anyString(), any(), anyString());
    }
}
