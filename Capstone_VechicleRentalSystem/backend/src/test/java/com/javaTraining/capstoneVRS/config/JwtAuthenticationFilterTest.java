package com.javaTraining.capstoneVRS.config;

import com.javaTraining.capstoneVRS.component.JwtComponent;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtComponent jwtComponent;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Initial setup
    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtComponent);
        SecurityContextHolder.clearContext();
    }

    // After each test, cleaup
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // Authentication test using Bearer header token
    @Test
    void authenticatesBearerTokenFromAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/vehicles");
        request.addHeader("Authorization", "Bearer bearer-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtComponent.validateToken("bearer-token")).thenReturn(true);
        when(jwtComponent.isTokenExpired("bearer-token")).thenReturn(false);
        when(jwtComponent.extractEmail("bearer-token")).thenReturn("aradhya@gmail.com");
        when(jwtComponent.extractRole("bearer-token")).thenReturn("ADMIN");

        invokeFilter(request, response);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("aradhya@gmail.com", authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
        verify(filterChain).doFilter(request, response);
    }

    // Authenticate using http-only Cookie
    @Test
    void authenticatesTokenFromAuthCookie() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/bookings");
        request.setCookies(new Cookie("authToken", "cookie-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtComponent.validateToken("cookie-token")).thenReturn(true);
        when(jwtComponent.isTokenExpired("cookie-token")).thenReturn(false);
        when(jwtComponent.extractEmail("cookie-token")).thenReturn("user@gmail.com");
        when(jwtComponent.extractRole("cookie-token")).thenReturn("USER");

        invokeFilter(request, response);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("user@gmail.com", authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
        verify(filterChain).doFilter(request, response);
    }

    // Mock request test without token
    @Test
    void passesThroughWhenNoTokenIsPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/users/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        invokeFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtComponent, never()).validateToken(org.mockito.ArgumentMatchers.anyString());
    }

    // Invalid token authentication
    @Test
    void skipsAuthenticationWhenTokenIsInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/bookings");
        request.addHeader("Authorization", "Bearer bad-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtComponent.validateToken("bad-token")).thenReturn(false);

        invokeFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    // Test helper to manually execute filter logic inside JwtAuthenticationFilter
    // for doFilterInternal() since it is a private method
    private void invokeFilter(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Method method = JwtAuthenticationFilter.class.getDeclaredMethod(
                "doFilterInternal",
                HttpServletRequest.class,
                HttpServletResponse.class,
                FilterChain.class);
        method.setAccessible(true);
        method.invoke(jwtAuthenticationFilter, request, response, filterChain);
    }
}