package com.javamentor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 * 
 * Features:
 * - CSRF protection enabled for web endpoints
 * - CSRF disabled for REST API endpoints (/api/**)
 * - No authentication (personal use)
 * - All endpoints accessible
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF: enable for web, disable for API
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")  // Ignore H2 console
                .ignoringRequestMatchers("/api/**")  // REST APIs use token/session auth
            )
            // Allow all requests (personal use, no authentication)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
            )
            // Allow H2 console frames
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );
        
        return http.build();
    }
}
