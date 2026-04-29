package com.javaTraining.capstoneVRS.component;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Token cookie for http only cookie, 
// localstorage is used for this system for now, this is for future. 
// In prod we send request to backend and it send the user object from httponly cookie containing jwt
// will try to implement that at last.

@Component
public class TokenCookieComponent {

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    public void setTokenCookie(HttpServletResponse response, String token) {
        // Convert milliseconds to seconds for Max-Age
        long maxAge = jwtExpiration / 1000;

        String cookieHeader = String.format(
                "authToken=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Strict",
                token,
                maxAge);
        response.addHeader("Set-Cookie", cookieHeader);
    }

    // Clear the token cookie by setting Max-Age to 0

    public void clearTokenCookie(HttpServletResponse response) {
        String cookieHeader = "authToken=; Path=/; Max-Age=0; HttpOnly; SameSite=Strict";
        response.addHeader("Set-Cookie", cookieHeader);
    }
}
