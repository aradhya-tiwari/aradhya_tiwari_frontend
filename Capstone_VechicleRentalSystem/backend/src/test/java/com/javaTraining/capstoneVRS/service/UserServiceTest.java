package com.javaTraining.capstoneVRS.service;

import com.javaTraining.capstoneVRS.component.JwtComponent;
import com.javaTraining.capstoneVRS.component.PasswordEncoderComponent;
import com.javaTraining.capstoneVRS.dto.request.LoginRequestDTO;
import com.javaTraining.capstoneVRS.dto.request.SignupRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.AuthResponseDTO;
import com.javaTraining.capstoneVRS.entity.User;
import com.javaTraining.capstoneVRS.entity.UserRole;
import com.javaTraining.capstoneVRS.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtComponent jwtComponent;

    @Mock
    private PasswordEncoderComponent passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, jwtComponent, passwordEncoder);
    }

    // Test user signup flow
    @Test
    void signup_createsUserAndReturnsToken() {
        SignupRequestDTO request = buildSignupRequest();
        User savedUser = buildUser(1L, request.getEmail());
        savedUser.setRole(UserRole.ADMIN);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encodePassword("secret1")).thenReturn("hashed-secret");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtComponent.generateToken(1L, request.getEmail(), UserRole.ADMIN.toString())).thenReturn("token-123");

        request.setRole(UserRole.ADMIN);
        AuthResponseDTO response = userService.signup(request);

        assertEquals("Signup successful", response.getMessage());
        assertEquals("token-123", response.getToken());
        assertEquals("Alice Driver", response.getUser().getFullName());
        assertEquals(UserRole.ADMIN, response.getUser().getRole());
        verify(passwordEncoder).encodePassword("secret1");
    }

    // Validation check unique email constraint
    @Test
    void signup_rejectsDuplicateEmail() {
        SignupRequestDTO request = buildSignupRequest();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> userService.signup(request));

        assertEquals("Email is already registered", error.getMessage());
    }

    // Signup request test when role is missing.
    @Test
    void signup_defaultsRoleToUserWhenRoleIsMissing() {
        SignupRequestDTO request = buildSignupRequest();
        request.setRole(null);
        User savedUser = buildUser(1L, request.getEmail());
        savedUser.setRole(UserRole.USER);

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encodePassword("secret1")).thenReturn("hashed-secret");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtComponent.generateToken(1L, request.getEmail(), UserRole.USER.toString())).thenReturn("token-123");

        AuthResponseDTO response = userService.signup(request);

        assertEquals(UserRole.USER, response.getUser().getRole());
    }

    // Return user upon login
    @Test
    void login_returnsAuthenticatedUser() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("alice@gmail.com");
        request.setPassword("secret1");

        User user = buildUser(1L, request.getEmail());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret1", "hashed-secret")).thenReturn(true);
        when(jwtComponent.generateToken(1L, request.getEmail(), UserRole.USER.toString())).thenReturn("token-456");

        AuthResponseDTO response = userService.login(request);
        // Return message of response
        assertEquals("Login successful", response.getMessage());
        // Returned using on when().thenReturn
        assertEquals("token-456", response.getToken());
        assertEquals(request.getEmail(), response.getUser().getEmail());
    }

    // Validation check for wrong password
    @Test
    void login_rejectsWrongPassword() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("alice@gmail.com");
        request.setPassword("wrong-password");

        User user = buildUser(1L, request.getEmail());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed-secret")).thenReturn(false);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> userService.login(request));

        assertEquals("Invalid email or password", error.getMessage());
    }

    //
    @Test
    void login_rejectsMissingUser() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("alice@gmail.com"); // Does not exist in @BeforeEach()
        request.setPassword("secret1");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> userService.login(request));

        assertEquals("Invalid email or password", error.getMessage());
    }

    private SignupRequestDTO buildSignupRequest() {
        SignupRequestDTO request = new SignupRequestDTO();
        request.setFullName("Alice Driver");
        request.setEmail("alice@gmail.com");
        request.setPassword("secret1");
        request.setRole(UserRole.USER);
        return request;
    }

    private User buildUser(Long id, String email) {
        User user = new User();
        user.setUserId(id);
        user.setFullName("Alice Driver");
        user.setEmail(email);
        user.setPasswordHash("hashed-secret");
        user.setRole(UserRole.USER);
        user.setIsActive(true);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        return user;
    }
}