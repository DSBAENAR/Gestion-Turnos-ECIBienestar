package com.shiftmanagement.app_core.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.Arrays;

/**
 * Configuration class for enabling and customizing CORS (Cross-Origin Resource Sharing) in a reactive Spring WebFlux application.
 */
@Configuration
public class WebConfig {

    /**
     * Defines the CORS configuration source, specifying allowed origins, headers, and methods.
     *
     * @return a {@link CorsConfigurationSource} with the configured CORS rules
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
            "https://shiftmanager-hrbgeaamdmg6ehb5.canadacentral-01.azurewebsites.net",
            "http://localhost:3000",
            "https://lively-rock-090f99110.6.azurestaticapps.net"
        ));
        config.addAllowedHeader("*");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    /**
     * Registers the {@link CorsWebFilter} that applies the configured CORS rules to all incoming HTTP requests.
     *
     * @param corsConfigurationSource the CORS configuration source bean
     * @return a {@link CorsWebFilter} instance
     */
    @Bean
    public CorsWebFilter corsWebFilter(CorsConfigurationSource corsConfigurationSource) {
        return new CorsWebFilter(corsConfigurationSource);
    }
}
