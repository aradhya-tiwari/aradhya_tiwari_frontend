package com.javaTraining.capstoneVRS.component;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Test of password hashing/encoding component 
class PasswordEncoderComponentTest {

    private final PasswordEncoderComponent passwordEncoderComponent = new PasswordEncoderComponent();

    @Test
    void encodePasswordProducesHashThatMatchesRawPassword() {
        String encoded = passwordEncoderComponent.encodePassword("secret1");

        assertNotEquals("secret1", encoded);
        assertTrue(passwordEncoderComponent.matches("secret1", encoded));
        assertFalse(passwordEncoderComponent.matches("wrong-password", encoded));
    }
}