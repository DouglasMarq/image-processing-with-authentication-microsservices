package com.douglasmarq.auth.infraestructure.utils;

import com.douglasmarq.auth.domain.dto.UserToken;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    private final SecretKey key =
            Keys.hmacShaKeyFor("My32ByteLongSuperSecretKeyForJWT!".getBytes());

    public String generateAccessToken(String email, UUID userId, String userPlan) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("userPlan", userPlan)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15)))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(String email, UUID userId, String userPlan) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("userPlan", userPlan)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)))
                .signWith(key)
                .compact();
    }

    public UserToken validateTokenAndGetData(String token) {
        var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        String email = claims.getSubject();
        String userId = claims.get("userId", String.class);
        String userPlan = claims.get("userPlan", String.class);

        var userToken = UserToken.builder();

        userToken.email(email);
        userToken.userId(UUID.fromString(userId));
        userToken.plan(userPlan);

        return userToken.build();
    }
}
