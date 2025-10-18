package com.finguard.userservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    public void generateToken_containsCorrectEmail() {
        String email = "user@example.com";
        List<String> roles = List.of("USER", "ADMIN");

        String token = jwtUtil.generateToken(email, roles);

        assertNotNull(token);
        assertEquals(email, jwtUtil.extractEmail(token));
    }

    @Test
    public void generateToken_containsRoles() {
        List<String> roles = List.of("USER", "ADMIN");
        String token = jwtUtil.generateToken("user@example.com", roles);

        List<String> extractedRoles = jwtUtil.extractRoles(token);

        assertNotNull(extractedRoles);
        assertTrue(extractedRoles.containsAll(roles));
    }

    @Test
    public void isTokenValid_returnsTrueForValidToken() {
        String token = jwtUtil.generateToken("user@example.com", List.of("USER"));

        assertTrue(jwtUtil.isTokenValid(token, "user@example.com"));
    }

    @Test
    public void extractEmail_returnsSubject() {
        String token = jwtUtil.generateToken("user@example.com", List.of("USER"));

        assertEquals("user@example.com", jwtUtil.extractEmail(token));
    }

    @Test
    public void extractRoles_returnsRoles() {
        List<String> roles = List.of("USER", "ADMIN");
        String token = jwtUtil.generateToken("user@example.com", roles);

        List<String> extracted = jwtUtil.extractRoles(token);

        assertEquals(roles.size(), extracted.size());
        assertTrue(extracted.containsAll(roles));
    }

    @Test
    public void isTokenValid_returnsFalseForDifferentEmail() {
        String token = jwtUtil.generateToken("user@example.com", List.of("USER"));

        assertFalse(jwtUtil.isTokenValid(token, "other@example.com"));
    }

    @Test
    public void isTokenExpired_returnsTrueForExpiredToken() throws InterruptedException {
        JwtUtil shortLivedJwt = new JwtUtil(1);
        String token = shortLivedJwt.generateToken("user@example.com", List.of("USER"));

        Thread.sleep(5);

        assertTrue(shortLivedJwt.isTokenExpired(token));
        assertFalse(shortLivedJwt.isTokenValid(token, "user@example.com"));
    }
}
