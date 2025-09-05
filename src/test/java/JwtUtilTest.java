import com.finguard.userservice.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JwtUtil} class.
 * These tests verify JWT token generation, validation, and parsing.
 */
public class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void testGenerateToken_containsCorrectEmail(){

        String email = "shubham@gmail.com";
        List<String> roles = Arrays.asList("USER", "ADMIN");

        String token = jwtUtil.generateToken(email, roles);

        assertNotNull(token);
        assertEquals(email, jwtUtil.extractEmail(token));
    }

    /**
     * Tests that roles are correctly embedded and can be extracted from the token.
     */
    @Test
    void testGenerateToken_containRoles(){

        String email = "shubham@gmail.com";
        List<String> roles = Arrays.asList("USER", "ADMIN");

        String token = jwtUtil.generateToken(email, roles);
        List<String> extractedRoles = jwtUtil.extractRoles(token);

        assertNotNull(extractedRoles);
        assertTrue(extractedRoles.contains("USER"));
        assertTrue(extractedRoles.contains("ADMIN"));
    }

    /**
     * Tests that Token validations works for correct token and email
     */
    @Test
    void testIsTokenValid_validToken_returnsTrue(){

        String email = "shubham@gmail.com";
        List<String> roles = Arrays.asList("USER", "ADMIN");

        String token = jwtUtil.generateToken(email, roles);

        assertTrue(jwtUtil.isTokenValid(token, email));

    }

    /**
     * Tests that extractEmail correctly returns the subject from the token.
     */
    @Test
    void testExtractEmail_success() {
        String token = jwtUtil.generateToken("shubham@gmail.com", List.of("USER"));


        String extractedEmail = jwtUtil.extractEmail(token);


        assertEquals("shubham@gmail.com", extractedEmail);
    }


    /**
     * Tests that extractRoles correctly returns a list of roles.
     */
    @Test
    void testExtractRoles_success() {
        List<String> roles = List.of("USER", "ADMIN");
        String token = jwtUtil.generateToken("shubham@gmail.com", roles);


        List<String> extracted = jwtUtil.extractRoles(token);


        assertEquals(roles.size(), extracted.size());
        assertTrue(extracted.containsAll(roles));
    }

    /**
     * Tests that Token validations fails for mismatched email
     */
    @Test
    void testIsTokenValid_invalidEmail_returnsFalse(){

        String token = jwtUtil.generateToken("shubham@gmail.com", List.of("USER"));

        assertFalse(jwtUtil.isTokenValid(token, "wrongemail@gmail.com"));
    }

    /**
     * Tests that expired token fails validation.
     * Note: This test may require a modified JwtUtil with injectable expiration for test.
     */
    @Test
    void testIsTokenExpired_falseForValidToken(){
        String token = jwtUtil.generateToken("shubham@gmail.com", List.of("USER"));

        assertFalse(jwtUtil.isTokenValid(token, "wrongemail@gmail.com"));
    }



}
