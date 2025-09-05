
import com.finguard.userservice.model.User;
import com.finguard.userservice.reporsitory.UserRepository;
import com.finguard.userservice.service.UserService;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserService}.
 */
@ExtendWith(MockitoExtension.class)  // Enables Mockito support in JUnit 5 tests
public class UserServiceTest {

    /**
     * Mocked repository for user persistence operations.
     */
    @Mock
    private UserRepository userRepository;

    /**
     * Mocked encoder for password hashing and verification.
     */
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Mocked utility for JWT token generation.
     */
    @Mock
    private JwtUtil jwtUtil;

    /**
     * The service under test, with mocked dependencies injected.
     */
    @InjectMocks  // This creates a real UserService instance with mocked dependencies
    private UserService userService;

  /*  @BeforeEach
     // If you use @ExtendWith(MockitoExtension.class), you don’t need a @BeforeEach to initialize mocks, because the extension handles it.
     //If you don’t use the Mockito extension, you must call MockitoAnnotations.openMocks(this)
     //in a @BeforeEach method to initialize mocks and enable dependency injection via @InjectMocks.
    void setup(){
        MockitoAnnotations.openMocks(this);
        // No need to manually create UserService - @InjectMocks handles it
    } */

    /**
     * Tests successful user registration.
     *
     * @throws AssertionError if any assertion fails.
     */
    @Test
    void testRegister_successful(){
        //Given
        User inputUser = new User();
        inputUser.setEmail("shubham@gamil.com");
        inputUser.setPassword("Shubham@123890");
        inputUser.setName("Shubham");
        inputUser.setRoles(Collections.singletonList("USER"));

        String hashedPassword = "hashedPass";
        when(bCryptPasswordEncoder.encode("Shubham@123890")).thenReturn("hashedPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //When
        User savedUser = userService.registerUser(inputUser);

        //Then
        assertEquals("shubham@gamil.com", savedUser.getEmail());
        assertEquals("Shubham", savedUser.getName());
        assertEquals(hashedPassword, savedUser.getPassword());
        verify(userRepository).save(any(User.class));
    }

    /**
     * Tests that {@code emailExists} returns true when the email is present.
     *
     * @throws AssertionError if the assertion fails.
     */
    @Test
    void testEmailExists_returnTrueIfExists(){
        //Given
        String email = "Shubham@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        //when
        boolean exists = userService.emailExists(email);

        // Then
        assertTrue(exists);
    }

    /**
     * Tests that {@code emailExists} returns false when the email is not present.
     *
     * @throws AssertionError if the assertion fails.
     */
    @Test
    void testEmailExists_returnFalseIfNotExists(){
        //Given
        String email = "Shubham123@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //When
        boolean exists = userService.emailExists(email);

        //Then
        assertFalse(exists);
    }

    /**
     * Tests that {@code findByEmail} returns an {@link Optional} containing the user when found.
     *
     * @throws AssertionError if any assertion fails.
     */
    @Test
    void testEmailExists_returnOptionalUser(){
        //Given
        String email = "Shubham@gmail.com";
        User mockUser = new User();
        mockUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        //When
        Optional<User> result = userService.findByEmail(email);

        //Then
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    /**
     * Tests successful login returns a valid JWT token.
     *
     * @throws AssertionError if any assertion fails.
     */
    @Test
    void testLoginSuccessful(){
        //Given
        String name = "Shubham";
        String email = "shubham@gmail.com";
        String rawPassword = "Shubham@123890";
        String hashedPassword = "hashedPassword";
        String jwtToken = "jwt.token.here";

        User mockUser = new User();
        mockUser.setName(name);
        mockUser.setEmail(email);
        mockUser.setPassword(hashedPassword);
        mockUser.setRoles(Collections.singletonList("USER"));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(bCryptPasswordEncoder.matches(rawPassword, hashedPassword)).thenReturn(true);
        when(jwtUtil.generateToken(email, Collections.singletonList("USER"))).thenReturn(jwtToken);

        //When
        String token = userService.login(email, rawPassword);

        //Then
        assertEquals(jwtToken, token);
        // verify() is used to ensure that specific methods on mocked dependencies are called with expected arguments during the test execution.
        verify(userRepository).findByEmail(email);
        verify(bCryptPasswordEncoder).matches(rawPassword,hashedPassword);
        verify(jwtUtil).generateToken(email, Collections.singletonList("USER"));

    }

    /**
     * Tests that registering a user with an already registered email throws an exception.
     */
    @Test
    void testRegister_duplicateEmail_throwsException(){
        User inputUser = new User();
        inputUser.setEmail("shubham@gmail.com");
        inputUser.setPassword("Shubham@123890");
        inputUser.setName("Shubham");

        when(userRepository.findByEmail("shubham@gmail.com")).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(RuntimeException.class, ()->{
            if(userService.emailExists(inputUser.getEmail())){
                throw new RuntimeException("Email already registered");
            }
            userService.registerUser(inputUser);
        });

        assertEquals("Email already registered" , exception.getMessage());
        verify(userRepository).findByEmail("shubham@gmail.com");
    }

    /**
     * Tests that login with an invalid email throws BadCredentialsException.
     */
    @Test
    void testLogin_invalidEmail_throwsException(){
        String email = "invalidUser@gamil.com";
        String rawPassword = "invalid@123890";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, ()->{
           userService.login(email, rawPassword);
        });

        verify(userRepository).findByEmail(email);
    }

    /**
     * Tests that login with incorrect password throws BadCredentialsException.
     */
    @Test
    void testLogin_invalidPassword_throwsException(){
        String email = "shubham@gmail.com";
        String rawPassword = "shubham";
        String hashedPassword = "hashedPassword";

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword(hashedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(bCryptPasswordEncoder.matches(rawPassword, hashedPassword)).thenReturn(false);

        assertThrows(BadCredentialsException.class , () ->{
           userService.login(email, rawPassword);
        });

        verify(userRepository).findByEmail(email);
        verify(bCryptPasswordEncoder).matches(rawPassword, hashedPassword);
    }

    /**
     * Tests that registering a user with null email or password throws IllegalArgumentException.
     */
    @Test
    void testRegister_nullEmail_throwsException(){

        User inputUser = new User();
        inputUser.setEmail(null);
        inputUser.setPassword("Shubham@123890");


        Exception exception = assertThrows(IllegalArgumentException.class, ()->{
           if(inputUser.getEmail() == null || inputUser.getPassword() == null ){
               throw new IllegalArgumentException("Email and Password cannot be null");
           }
           userService.registerUser(inputUser);
        });

        assertEquals("Email and Password cannot be null", exception.getMessage());
    }


    /**
     * Tests that {@code loadUserByUsername} returns a valid {@link UserDetails} object
     * when the user exists in the database.
     *
     * <p>This test verifies that:</p>
     * <ul>
     *   <li>The returned username and password match the stored user.</li>
     *   <li>The returned authorities contain the expected role (e.g., ROLE_USER).</li>
     * </ul>
     *
     * <p>Note: ROLE_ prefix is automatically added by Spring Security for roles.</p>
     */
    @Test
    void testLoadUserByUsername_success() {
        User user = new User();
        user.setEmail("shubham@gamil.com");
        user.setPassword("hashedPassword");
        user.setRoles(Collections.singletonList("USER"));

        when(userRepository.findByEmail("shubham@gmail.com")).thenReturn(Optional.of(user));

        UserDetails details = userService.loadUserByUsername("shubham@gmail.com");

        assertEquals("shubham@gamil.com", details.getUsername());
        assertEquals("hashedPassword", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    /**
     * Tests that {@code loadUserByUsername} throws a {@link UsernameNotFoundException}
     * when the user does not exist in the database.
     *
     * <p>This ensures proper exception handling during authentication for nonexistent users.</p>
     */
    @Test
    void testLoadUserByUsername_userNotFound() {
        when(userRepository.findByEmail("shubham@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("shubham@gmail.com");
        });
    }

}
