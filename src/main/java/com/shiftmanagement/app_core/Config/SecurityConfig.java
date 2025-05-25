package com.shiftmanagement.app_core.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.ServerHttpSecurity;

/**
 * Configuration class for reactive Spring Security in a WebFlux application.
 * This setup disables CSRF protection and allows unrestricted access to all endpoints,
 * including Swagger documentation paths.
 */
@Configuration
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application.
     * - Disables CSRF protection.
     * - Permits all requests, including Swagger UI and OpenAPI endpoints.
     *
     * @param http the {@link ServerHttpSecurity} object used to configure security
     * @return a {@link SecurityWebFilterChain} instance with the configured security rules
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchange -> exchange
                .pathMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyExchange().permitAll()
            )
            .build();
    }
}
