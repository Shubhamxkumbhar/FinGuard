package com.finguard.userservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * JwtUtil is a utility class for generating, parsing, and validating JSON Web Tokens (JWT).
 * Uses a symmetric key (HMAC-SHA) to sign tokens.
 * It helps in creating secure, stateless authentication for users by embedding user identity and roles inside the token.
 *
 * Note: For now, the key is kept in memory, but in production,
 *       you’d use an environment variable or secure vault
 *       (like AWS Secrets Manager or Spring Config Server).
 */

@Component
public class JwtUtil {

    // Secret key for signing the token (in real-world apps, load from environment/secure vault)
    // Keys.secretKeyFor(SignatureAlgorithm.HS256): Generates a secure HMAC key.
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // // Token validity: 24 hours
    private final long expirationMillis = 1000 * 60 * 60 * 24;

    /**
     * Generates a JWT token for a given email and list of user roles.
     *
     * @param email the user's email (used as the subject of the token)
     * @param roles the list of roles assigned to the user
     * @return a signed JWT token string
     */
    public String generateToken(String email, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(email)              // Set email as the subject claim
                .claim("roles",roles)       // Add custom claim for roles
                .setIssuedAt(new Date())       // Current time (token creation time)
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))     // Expiration time
                .signWith(secretKey)     // Sign using HS256 and secret key
                .compact();             // Build and return token as string
    }

    /**
     * Extracts the email (subject) from the given token.
     *
     * @param token the JWT token
     * @return the email stored in the subject claim
     */
    public String extractEmail(String token) {
        return parseToken(token).getBody().getSubject();
    }

    /**
     * Extracts the user roles from the given token.
     *
     * @param token the JWT token
     * @return the list of roles extracted from the "roles" claim
     */
    public List extractRoles(String token) {
        return parseToken(token).getBody().get("roles", List.class);
    }

    /**
     * Validates a token by checking:
     * 1. Whether it belongs to the given email.
     * 2. Whether it has expired or not.
     *
     * @param token the JWT token to validate
     * @param email the email to match against the token's subject
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token, String email) {
        String extractedEmail = extractEmail(token);
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }

    /**
     * Checks if the token has expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return parseToken(token).getBody().getExpiration().before(new Date());
    }

    /**
     * Parses and validates the JWT token using the secret key.
     *
     * @param token the JWT token
     * @return parsed claims (payload) from the token
     */
    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)           // Set the key for signature validation
                .build()
                .parseClaimsJws(token);             // This also validates the signature
    }
}
