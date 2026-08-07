package com.testpilot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CorsConfig
 *
 * CORS = Cross-Origin Resource Sharing.
 * When the React app (running on http://localhost:5173) makes requests
 * to the Spring Boot backend (http://localhost:8080), the browser blocks
 * the request by default because they are on different ports (different origins).
 *
 * This configuration tells Spring Boot to ALLOW requests from the React frontend.
 * Without this, you will get a "CORS policy" error in the browser console.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from the React dev server
        config.addAllowedOrigin("http://localhost:5173");

        // Allow all HTTP headers (Content-Type, Authorization, etc.)
        config.addAllowedHeader("*");

        // Allow all HTTP methods (GET, POST, PUT, DELETE, OPTIONS)
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Apply this CORS config to all endpoints in the application
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
