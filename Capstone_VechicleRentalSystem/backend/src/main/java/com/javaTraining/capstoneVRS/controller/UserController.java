package com.javaTraining.capstoneVRS.controller;

import com.javaTraining.capstoneVRS.dto.request.LoginRequestDTO;
import com.javaTraining.capstoneVRS.dto.request.SignupRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.AuthResponseDTO;
import com.javaTraining.capstoneVRS.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDTO request, HttpServletResponse response) {
        try {
            AuthResponseDTO authResponse = userService.signup(request);
            setTokenCookie(response, authResponse.getToken());

            // Remove token from response body (it's in the cookie)
            authResponse.setToken(null);

            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request, HttpServletResponse response) {
        try {
            AuthResponseDTO authResponse = userService.login(request);
            setTokenCookie(response, authResponse.getToken());

            // Remove token from response body (it's in the cookie)
            authResponse.setToken(null);

            return ResponseEntity.ok(authResponse);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    private void setTokenCookie(HttpServletResponse response, String token) {
        response.addHeader("Set-Cookie",
                String.format("authToken=%s; Path=/; Max-Age=86400; HttpOnly; SameSite=Strict", token));
    }

}
