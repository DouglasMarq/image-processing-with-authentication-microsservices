package com.douglasmarq.auth.application.controller;

import com.douglasmarq.auth.domain.dto.RegisterUserRequest;
import com.douglasmarq.auth.domain.service.RegisterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/register")
@AllArgsConstructor
@Tag(name = "Registration", description = "User Registration API")
@Slf4j
public class RegisterController {
    private final RegisterService registerService;

    @Operation(
            summary = "Register new user",
            description = "Creates a new user account in the system")
    @ApiResponse(responseCode = "201", description = "User successfully registered")
    @ApiResponse(responseCode = "400", description = "Invalid registration data")
    @ApiResponse(responseCode = "409", description = "User exists")
    @PostMapping
    public ResponseEntity register(@Valid @RequestBody RegisterUserRequest request) {
        log.info("Receiving a new request to register user.");

        registerService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
