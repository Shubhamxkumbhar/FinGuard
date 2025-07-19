package com.finguard.userservice.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for generating JWT tokens.
 * Uses a symmetric key (HMAC-SHA) to sign tokens.
 *
 * Note: For now, the key is kept in memory, but in production,
 *       you’d use an environment variable or secure vault (like AWS Secrets Manager or Spring Config Server).
 */
@Component
public class JwtUtil {

    // Secret key for signing the token (in real-world apps, load from environment/secure vault)
    // Keys.secretKeyFor(SignatureAlgorithm.HS256): Generates a secure HMAC key.
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token validity (e.g., 1 hour)
    private final long expirationMillis = 1000 * 60 * 60;

    /**
     * Generates a JWT token for the given subject (email/username).
     * generateToken(): Signs the payload (email) and sets an expiration time.
     * @param subject the unique identifier for the user (typically email)
     * @return a signed JWT token string
     */
    public String generateToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }
}
