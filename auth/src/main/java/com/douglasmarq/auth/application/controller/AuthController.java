package com.douglasmarq.auth.application.controller;

import com.douglasmarq.auth.domain.dto.TokenResponse;
import com.douglasmarq.auth.domain.dto.UserAuthRequest;
import com.douglasmarq.auth.domain.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@AllArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Login to the system",
            description = "Authenticates a user and returns a JWT token")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully authenticated",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class)))
    @ApiResponse(responseCode = "400", description = "User not found or password is incorrect")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody UserAuthRequest body) {
        return ResponseEntity.ok(authService.authenticate(body.getEmail(), body.getPassword()));
    }

    @Operation(
            summary = "Refresh authentication token",
            description = "Generates a new JWT token using a valid refresh token")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully refreshed token",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TokenResponse.class)))
    @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }
}
