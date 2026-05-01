package com.javaTraining.capstoneVRS.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtComponentTest {

    private JwtComponent jwtComponent;

    // For every test for consistency
    @BeforeEach
    void setUp() {
        // JWT component to test
        jwtComponent = new JwtComponent();
        setField("jwtSecret", "1234567890123456789012345678901234567890123456789012345678901234");
        setField("jwtExpiration", 60_000L);
    }

    // @Test to define test
    @Test
    void generateTokenAndExtractClaimsWork() {
        String token = jwtComponent.generateToken(7L, "user@example.com", "USER");
        // assert to check for condition
        assertTrue(jwtComponent.validateToken(token));
        assertEquals(7L, jwtComponent.extractUserId(token));
        assertEquals("user@example.com", jwtComponent.extractEmail(token));
        assertEquals("USER", jwtComponent.extractRole(token));
        assertFalse(jwtComponent.isTokenExpired(token));
    }

    @Test
    void invalidTokenFailsValidation() {
        assertFalse(jwtComponent.validateToken("not-a-token"));
        assertTrue(jwtComponent.isTokenExpired("not-a-token"));
    }

    @Test
    void expiredTokenIsReportedAsExpired() {
        setField("jwtExpiration", -1_000L);
        String token = jwtComponent.generateToken(7L, "user@example.com", "USER");

        assertTrue(jwtComponent.isTokenExpired(token));
    }

    private void setField(String fieldName, Object value) {
        try {
            Field field = JwtComponent.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(jwtComponent, value);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to set field: " + fieldName, exception);
        }
    }
}