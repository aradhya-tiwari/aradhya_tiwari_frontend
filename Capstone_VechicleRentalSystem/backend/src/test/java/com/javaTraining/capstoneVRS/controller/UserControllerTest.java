package com.javaTraining.capstoneVRS.controller;

import com.javaTraining.capstoneVRS.component.TokenCookieComponent;
import com.javaTraining.capstoneVRS.dto.request.LoginRequestDTO;
import com.javaTraining.capstoneVRS.dto.request.SignupRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.AuthResponseDTO;
import com.javaTraining.capstoneVRS.dto.response.UserResponseDTO;
import com.javaTraining.capstoneVRS.entity.UserRole;
import com.javaTraining.capstoneVRS.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    // Mocking service
    @Mock
    private UserService userService;

    @Mock
    private TokenCookieComponent tokenCookieComponent;

    @Mock
    private HttpServletResponse httpServletResponse;

    // Controllers are not mocked
    private UserController userController;

    // Initial setup
    @BeforeEach
    void setUp() {
        userController = new UserController(userService, tokenCookieComponent);
    }

    // Signup test
    @Test
    void signup_returnsCreatedAndSetsCookie() {
        SignupRequestDTO request = buildSignupRequest();
        AuthResponseDTO authResponse = buildAuthResponse("Signup successful", "signup-token");

        when(userService.signup(request)).thenReturn(authResponse);

        ResponseEntity<?> response = userController.signup(request, httpServletResponse);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
        verify(tokenCookieComponent).setTokenCookie(httpServletResponse, "signup-token");
    }

    // Test for signup when user's email already exists
    @Test
    void signup_returnsBadRequestWhenServiceRejectsRequest() {
        SignupRequestDTO request = buildSignupRequest();
        when(userService.signup(request)).thenThrow(new IllegalArgumentException("Email is already registered"));

        ResponseEntity<?> response = userController.signup(request, httpServletResponse);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email is already registered", messageOf(response));
    }

    // Test for successful login
    @Test
    void login_returnsOkAndSetsCookie() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("alice@gmail.com");
        request.setPassword("secret1");
        AuthResponseDTO authResponse = buildAuthResponse("Login successful", "login-token");

        when(userService.login(request)).thenReturn(authResponse);

        ResponseEntity<?> response = userController.login(request, httpServletResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
        verify(tokenCookieComponent).setTokenCookie(httpServletResponse, "login-token");
    }

    // Test for login with wrong credentials
    @Test
    void login_returnsUnauthorizedWhenServiceRejectsRequest() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("alice@gmail.com");
        request.setPassword("wrong-password");
        when(userService.login(request)).thenThrow(new IllegalArgumentException("Invalid email or password"));

        ResponseEntity<?> response = userController.login(request, httpServletResponse);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", messageOf(response));
    }

    // Signup request DTO
    private SignupRequestDTO buildSignupRequest() {
        SignupRequestDTO request = new SignupRequestDTO();
        request.setFullName("Alice Driver");
        request.setEmail("alice@gmail.com");
        request.setPassword("secret1");
        request.setRole(UserRole.USER);
        return request;
    }

    // Auth response DTO
    private AuthResponseDTO buildAuthResponse(String message, String token) {
        UserResponseDTO user = new UserResponseDTO();
        user.setUserId(1L);
        user.setFullName("Alice Driver");
        user.setEmail("alice@gmail.com");
        user.setRole(UserRole.USER);

        AuthResponseDTO response = new AuthResponseDTO();
        response.setMessage(message);
        response.setToken(token);
        response.setUser(user);
        return response;
    }

    // To Extract response message
    private String messageOf(ResponseEntity<?> response) {
        Map<?, ?> body = assertInstanceOf(Map.class, response.getBody());
        return body.get("message").toString();
    }
}
