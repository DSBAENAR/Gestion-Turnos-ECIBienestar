package com.shiftmanagement.app_core.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.shiftmanagement.app_core.model.JwtResponse;

@Service
public class JwtWebClientService {

    private final WebClient webClient;

    private String cachedToken;
    private Instant expiresAt = Instant.EPOCH;

    @Value("${api.auth.url}")
    private String Url;

    @Value("${api.auth.username}")
    private String username;

    @Value("${api.auth.password}")
    private String password;

    public JwtWebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String getToken() {
        if (cachedToken == null || Instant.now().isAfter(expiresAt)) {
            refreshToken();
        }
        return cachedToken;
    }
    
    private void refreshToken() {
        Map<String, String> loginRequest = Map.of(
            "userId", username,
            "password", password
        );

        JwtResponse response = webClient.post()
            .uri(Url + "/user-service/login")
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(loginRequest)
            .retrieve()
            .bodyToMono(JwtResponse.class)
            .block();

        cachedToken = response.getToken();
        expiresAt = Instant.now().plus(Duration.ofMinutes(55));
    }
}

