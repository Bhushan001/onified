package com.onified.ai.authentication_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs; // in milliseconds

    private Algorithm algorithm;

    // Initialize algorithm once secret is injected
    private Algorithm getAlgorithm() {
        if (algorithm == null) {
            this.algorithm = Algorithm.HMAC256(secret);
        }
        return algorithm;
    }

    public String generateToken(UUID userId, String username, List<String> roles) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(expirationMs, ChronoUnit.MILLIS);

        return JWT.create()
                .withSubject(userId.toString()) // Unique identifier for the user
                .withClaim("username", username)
                .withClaim("roles", roles) // Include user roles
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiryDate))
                .sign(getAlgorithm());
    }

    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        return JWT.require(getAlgorithm())
                .build()
                .verify(token);
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(validateToken(token).getSubject());
    }

    public String getUsernameFromToken(String token) {
        return validateToken(token).getClaim("username").asString();
    }

    public List<String> getRolesFromToken(String token) {
        return validateToken(token).getClaim("roles").asList(String.class);
    }
}