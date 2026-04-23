package com.javaTraining.capstoneVRS.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//Cors Configurations implements WebMvcConfigurer to allow all requests to /api/...
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // frontend url can be loaded from application.properties using @Value notation
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:5500",
                        "http://localhost:5173",
                        // main origin
                        "http://127.0.0.1:5500",
                        // if dev server used in future so port 5173
                        "http://127.0.0.1:5173")
                // http verb allowed to query
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // allowing all headers
                .allowedHeaders("*")
                // allow cookie based credentials
                .allowCredentials(true)
                .maxAge(3600);
    }
}
