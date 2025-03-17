package com.douglasmarq.auth.application.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.douglasmarq.auth.domain.PlanType;
import com.douglasmarq.auth.domain.Users;
import com.douglasmarq.auth.domain.dto.TokenResponse;
import com.douglasmarq.auth.domain.dto.UserAuthRequest;
import com.douglasmarq.auth.domain.dto.UserToken;
import com.douglasmarq.auth.domain.repository.UserRepository;
import com.douglasmarq.auth.domain.service.AuthService;
import com.douglasmarq.auth.infraestructure.utils.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AuthControllerTestIT {
    @Mock private UserRepository userRepository;

    @Mock private JwtUtil jwtUtil;

    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        if (authService == null) {
            authService = buildAuthService();
        }
        if (authController == null) {
            authController = new AuthController(authService);
        }
        Mockito.reset(userRepository);
        Mockito.reset(jwtUtil);
    }

    private AuthService buildAuthService() {
        return new AuthService(userRepository, jwtUtil);
    }

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

    private UserToken createValidUserToken(UUID userId) {
        return UserToken.builder()
                .userId(userId)
                .email("test@example.com")
                .plan(PlanType.BASIC.toString())
                .build();
    }

    @Test
    @DisplayName(
            "Should succesfully generate an access token and refresh token upon successful login")
    public void shouldSuccessfullyGenerateAnAccessTokenAndRefreshTokenUponSuccessfulLogin() {
        var validUser = createValidUser();

        UserAuthRequest authRequest =
                new UserAuthRequest(validUser.getEmail(), validUser.getPassword());

        when(userRepository.findByEmail(validUser.getEmail())).thenReturn(Optional.of(validUser));

        String accessToken = "mockAccessToken";
        String refreshToken = "mockRefreshToken";
        TokenResponse expectedResponse = new TokenResponse(accessToken, refreshToken);

        when(jwtUtil.generateAccessToken(
                        validUser.getEmail(), validUser.getId(), validUser.getPlan().toString()))
                .thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(
                        validUser.getEmail(), validUser.getId(), validUser.getPlan().toString()))
                .thenReturn(refreshToken);

        ResponseEntity<TokenResponse> response = authController.login(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(accessToken, response.getBody().getAccessToken());
        assertEquals(refreshToken, response.getBody().getRefreshToken());
        verify(userRepository, times(1)).findByEmail(validUser.getEmail());
    }

    @Test
    @DisplayName(
            "Should successfully refresh an access token and refresh token upon successful validation")
    public void shouldSuccessfullyRefreshAnAccessTokenAndRefreshTokenUponSuccessfulValidation() {
        var validUser = createValidUser();
        var validUserToken = createValidUserToken(validUser.getId());

        String refreshToken = "validRefreshToken";

        when(jwtUtil.validateTokenAndGetData(refreshToken)).thenReturn(validUserToken);

        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";
        when(jwtUtil.generateAccessToken(
                        validUser.getEmail(), validUser.getId(), validUser.getPlan().toString()))
                .thenReturn(newAccessToken);
        when(jwtUtil.generateRefreshToken(
                        validUser.getEmail(), validUser.getId(), validUser.getPlan().toString()))
                .thenReturn(newRefreshToken);

        ResponseEntity<TokenResponse> response = authController.refresh(refreshToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newAccessToken, response.getBody().getAccessToken());
        assertEquals(newRefreshToken, response.getBody().getRefreshToken());
        verify(jwtUtil, times(1)).validateTokenAndGetData(refreshToken);
    }
}
