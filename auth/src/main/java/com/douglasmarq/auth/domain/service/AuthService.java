package com.douglasmarq.auth.domain.service;

import com.douglasmarq.auth.domain.dto.TokenResponse;
import com.douglasmarq.auth.domain.dto.UserToken;
import com.douglasmarq.auth.domain.exception.UserApiException;
import com.douglasmarq.auth.domain.repository.UserRepository;
import com.douglasmarq.auth.infraestructure.logs.AnonymizeLogger;
import com.douglasmarq.auth.infraestructure.utils.JwtUtil;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final AnonymizeLogger logger = new AnonymizeLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordService passwordService;

    public TokenResponse authenticate(String email, String password) {
        var user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            logger.error("User not found or password is incorrect for {}", email);
            throw new UserApiException(
                    "User not found or password is incorrect", HttpStatus.BAD_REQUEST);
        }

        var userResult = user.get();

        if (passwordService.decode(password, userResult.getPassword())) {
            String accessToken =
                    jwtUtil.generateAccessToken(
                            userResult.getEmail(),
                            userResult.getId(),
                            userResult.getPlan().toString());
            String refreshToken =
                    jwtUtil.generateRefreshToken(
                            userResult.getEmail(),
                            userResult.getId(),
                            userResult.getPlan().toString());

            logger.info("User {} authenticated successfully", email);

            return new TokenResponse(accessToken, refreshToken);
        }

        logger.error("User not found or password is incorrect for {}", email);

        throw new UserApiException(
                "User not found or password is incorrect", HttpStatus.BAD_REQUEST);
    }

    public TokenResponse refresh(String refreshToken) {
        logger.info("Refreshing user token");

        UserToken user = jwtUtil.validateTokenAndGetData(refreshToken);

        String newAccessToken =
                jwtUtil.generateAccessToken(user.getEmail(), user.getUserId(), user.getPlan());
        String newRefreshToken =
                jwtUtil.generateRefreshToken(user.getEmail(), user.getUserId(), user.getPlan());

        logger.info("User token refreshed successfully");

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
