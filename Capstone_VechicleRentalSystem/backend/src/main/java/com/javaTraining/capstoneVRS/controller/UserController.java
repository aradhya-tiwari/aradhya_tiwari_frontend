package com.javaTraining.capstoneVRS.controller;

import com.javaTraining.capstoneVRS.component.TokenCookieComponent;
import com.javaTraining.capstoneVRS.dto.request.LoginRequestDTO;
import com.javaTraining.capstoneVRS.dto.request.SignupRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.AuthResponseDTO;
import com.javaTraining.capstoneVRS.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final TokenCookieComponent tokenCookieComponent;

    public UserController(UserService userService, TokenCookieComponent tokenCookieComponent) {
        this.userService = userService;
        this.tokenCookieComponent = tokenCookieComponent;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDTO request, HttpServletResponse response) {
        try {
            log.info("Signup request received for email={}", request.getEmail());
            AuthResponseDTO authResponse = userService.signup(request);
            tokenCookieComponent.setTokenCookie(response, authResponse.getToken());
            log.info("Signup completed for userId={} email={}",
                    authResponse.getUser().getUserId(),
                    authResponse.getUser().getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (IllegalArgumentException ex) {
            log.warn("Signup failed for email={} reason={}", request.getEmail(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request, HttpServletResponse response) {
        try {
            log.info("Login request received for email={}", request.getEmail());
            AuthResponseDTO authResponse = userService.login(request);
            tokenCookieComponent.setTokenCookie(response, authResponse.getToken());
            log.info("Login completed for userId={} email={}",
                    authResponse.getUser().getUserId(),
                    authResponse.getUser().getEmail());

            return ResponseEntity.ok(authResponse);
        } catch (IllegalArgumentException ex) {
            log.warn("Login failed for email={} reason={}", request.getEmail(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", ex.getMessage()));
        }
    }
}
