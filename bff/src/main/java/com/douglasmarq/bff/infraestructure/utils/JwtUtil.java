package com.douglasmarq.bff.infraestructure.utils;

import com.douglasmarq.bff.domain.dto.UserToken;
import com.douglasmarq.bff.domain.exception.SignatureException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.http.HttpStatus;

import java.util.UUID;

import javax.crypto.SecretKey;

public class JwtUtil {
    private static final SecretKey key =
            Keys.hmacShaKeyFor("My32ByteLongSuperSecretKeyForJWT!".getBytes());

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);

            return true;
        } catch (Exception e) {
            throw new SignatureException("Invalid token", HttpStatus.UNAUTHORIZED);
        }
    }

    public static String getSubject(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public static UserToken parseTokenClaimsIntoMap(String token) {
        var jwt = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        var userTokenBuilder =
                UserToken.builder()
                        .email(jwt.getSubject())
                        .userId(UUID.fromString(jwt.get("userId", String.class)))
                        .plan(jwt.get("userPlan", String.class));

        return userTokenBuilder.build();
    }
}
