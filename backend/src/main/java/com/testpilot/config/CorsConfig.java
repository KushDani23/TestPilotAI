package com.testpilot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    /**
     * ALLOWED_ORIGIN env var is set in the Render dashboard to the Vercel frontend URL.
     * Falls back to localhost:5173 for local development.
     */
    @Value("${allowed.origin:http://localhost:5173}")
    private String allowedOrigin;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // Always allow localhost for dev; also allow the production Vercel origin
        config.setAllowedOrigins(List.of("http://localhost:5173", allowedOrigin));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
