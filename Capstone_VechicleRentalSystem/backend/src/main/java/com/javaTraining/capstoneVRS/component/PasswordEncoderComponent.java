package com.javaTraining.capstoneVRS.component;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

// Hashing password using bcrypt
@Component
public class PasswordEncoderComponent {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Hashing password
    public String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    // Checking hash match, this process checks hash instead of decoding and
    // comparing
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
