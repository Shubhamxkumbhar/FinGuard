import com.finguard.userservice.dto.UserRegistrationRequest;
import com.finguard.userservice.model.User;
import com.finguard.userservice.reporsitory.UserRepository;
import com.finguard.userservice.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link com.finguard.userservice.service.UserService}.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private com.finguard.userservice.service.UserService userService;

    @Test
    public void registerUser_successful() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "Shubham",
                "shubham@gmail.com",
                "PlainPassword1",
                List.of("admin", "USER")
        );

        when(userRepository.findByEmail("shubham@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("PlainPassword1")).thenReturn("hashedPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.registerUser(request);

        assertEquals("shubham@gmail.com", saved.getEmail());
        assertEquals("hashedPass", saved.getPassword());
        assertEquals(List.of("ADMIN", "USER"), saved.getRoles());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void registerUser_assignsDefaultRoleWhenMissing() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "Shubham",
                "user@example.com",
                "PlainPassword1",
                null
        );

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("PlainPassword1")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = userService.registerUser(request);

        assertEquals(List.of("USER"), saved.getRoles());
    }

    @Test
    public void registerUser_duplicateEmail_throwsException() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "Shubham",
                "taken@example.com",
                "PlainPassword1",
                List.of("USER")
        );

        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(new User()));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> userService.registerUser(request));
        assertEquals("Email already registered.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void registerUser_missingEmail_throwsException() {
        User user = new User();
        user.setPassword("password");

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void emailExists_returnsTrueWhenEmailPresent() {
        when(userRepository.findByEmail("exists@example.com")).thenReturn(Optional.of(new User()));

        assertTrue(userService.emailExists("exists@example.com"));
    }

    @Test
    public void emailExists_returnsFalseWhenEmailMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertFalse(userService.emailExists("missing@example.com"));
    }

    @Test
    public void findByEmail_returnsUserWhenPresent() {
        User user = new User();
        user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("user@example.com");

        assertTrue(result.isPresent());
        assertEquals("user@example.com", result.get().getEmail());
    }

    @Test
    public void login_successfulReturnsToken() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("hashed");
        user.setRoles(List.of("USER"));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("PlainPassword1", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken("user@example.com", List.of("USER"))).thenReturn("jwt-token");

        String token = userService.login("user@example.com", "PlainPassword1");

        assertEquals("jwt-token", token);
    }

    @Test
    public void login_defaultsRoleWhenMissing() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("hashed");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("PlainPassword1", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(eq("user@example.com"), eq(List.of("USER")))).thenReturn("jwt-token");

        String token = userService.login("user@example.com", "PlainPassword1");

        assertEquals("jwt-token", token);
    }

    @Test
    public void login_invalidEmail_throwsException() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> userService.login("missing@example.com", "password"));
    }

    @Test
    public void login_invalidPassword_throwsException() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("hashed");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.login("user@example.com", "wrong"));
    }

    @Test
    public void loadUserByUsername_success() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("hashed");
        user.setRoles(List.of("USER"));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDetails details = userService.loadUserByUsername("user@example.com");

        assertEquals("user@example.com", details.getUsername());
        assertEquals("hashed", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    public void loadUserByUsername_userNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("missing@example.com"));
    }
}
