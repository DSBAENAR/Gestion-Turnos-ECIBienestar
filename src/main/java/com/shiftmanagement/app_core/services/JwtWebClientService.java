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

import reactor.core.publisher.Mono;

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

    public Mono<String> getToken(String id) {
    if (cachedToken == null || Instant.now().isAfter(expiresAt)) {
        return refreshToken(id);
    }
    return Mono.just(cachedToken);
}

private Mono<String> refreshToken(String id) {
    return webClient.get()
        .uri(Url + "/user-service/users/" + id)
        .retrieve()
        .bodyToMono(User.class)
        .flatMap(user -> {
            if (user == null || user.userName() == null || user.password() == null) {
                return Mono.error(new IllegalStateException("Credenciales faltantes para usuario " + id));
            }

            Map<String, String> loginRequest = Map.of(
                "userId", user.userName(),
                "password", user.password()
            );

            return webClient.post()
                .uri(Url + "/user-service/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(JwtResponse.class)
                .flatMap(jwt -> {
                    if (jwt.getToken() == null) {
                        return Mono.error(new IllegalStateException("El token JWT es null"));
                    }
                    this.cachedToken = jwt.getToken();
                    this.expiresAt = Instant.now().plus(Duration.ofMinutes(55));
                    return Mono.just(jwt.getToken());
                });
        });
    }
}

