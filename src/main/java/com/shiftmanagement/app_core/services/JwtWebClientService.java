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
import com.shiftmanagement.app_core.model.User;

@Service
public class JwtWebClientService {

    private final WebClient webClient;

    private String cachedToken;
    private Instant expiresAt = Instant.EPOCH;

    @Value("${api.auth.url}")
    private String Url;

    public JwtWebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String getToken(String id) {
        if (cachedToken == null || Instant.now().isAfter(expiresAt)) {
            refreshToken(id);
        }
        return cachedToken;
    }
    
    private void refreshToken(String id) {
        
        User user = webClient.get()
        .uri(Url + "/user-service/users/" + id)
        .retrieve()
        .bodyToMono(User.class)
        .block();

    if (user == null || user.userName() == null || user.password() == null) {
        throw new IllegalStateException("Credenciales incompletas para usuario " + id);
    }
        Map<String, String> loginRequest = Map.of(
            "userName", user.userName(),
            "password", user.password()
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

