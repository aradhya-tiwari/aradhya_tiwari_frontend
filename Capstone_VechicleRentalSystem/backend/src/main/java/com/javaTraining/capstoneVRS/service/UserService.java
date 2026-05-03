package com.javaTraining.capstoneVRS.service;

import com.javaTraining.capstoneVRS.component.JwtComponent;
import com.javaTraining.capstoneVRS.component.PasswordEncoderComponent;
import com.javaTraining.capstoneVRS.dto.request.LoginRequestDTO;
import com.javaTraining.capstoneVRS.dto.request.SignupRequestDTO;
import com.javaTraining.capstoneVRS.dto.response.AuthResponseDTO;
import com.javaTraining.capstoneVRS.dto.response.UserResponseDTO;
import com.javaTraining.capstoneVRS.entity.User;
import com.javaTraining.capstoneVRS.entity.UserRole;
import com.javaTraining.capstoneVRS.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepo;
    private final JwtComponent jwtComponent;
    private final PasswordEncoderComponent passwordEncoder;

    public UserService(UserRepository userRepo, JwtComponent jwtComponent, PasswordEncoderComponent passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtComponent = jwtComponent;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO signup(SignupRequestDTO request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            log.warn("Signup rejected because email already exists email={}", request.getEmail());
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        // Hash password using bcrypt
        user.setPasswordHash(passwordEncoder.encodePassword(request.getPassword()));
        user.setRole(request.getRole() == null ? UserRole.USER : request.getRole());
        user.setIsActive(true);

        OffsetDateTime now = OffsetDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User savedUser = userRepo.save(user);
        log.info("User registered userId={} email={} role={}",
                savedUser.getUserId(),
                savedUser.getEmail(),
                savedUser.getRole());

        // Generate JWT token with role
        String token = jwtComponent.generateToken(savedUser.getUserId(), savedUser.getEmail(),
                savedUser.getRole().toString());
        AuthResponseDTO response = new AuthResponseDTO();
        response.setMessage("Signup successful");
        response.setUser(toUserResponse(savedUser));
        response.setToken(token);
        return response;
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Verify password using bcrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Login rejected because password did not match email={}", request.getEmail());
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate JWT token with role
        String token = jwtComponent.generateToken(user.getUserId(), user.getEmail(), user.getRole().toString());
        log.info("User authenticated userId={} email={} role={}",
                user.getUserId(),
                user.getEmail(),
                user.getRole());
        log.debug("JWT generated for userId={}", user.getUserId());
        AuthResponseDTO response = new AuthResponseDTO();
        response.setMessage("Login successful");
        response.setUser(toUserResponse(user));
        response.setToken(token);
        return response;
    }

    private UserResponseDTO toUserResponse(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
