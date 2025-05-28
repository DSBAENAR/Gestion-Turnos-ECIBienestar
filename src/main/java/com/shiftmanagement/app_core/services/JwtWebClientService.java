package com.shiftmanagement.app_core.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    /**
     * Retrieves a valid JWT token for the specified user.
     * If a token is cached and not expired, it returns it; otherwise, it fetches a new one.
     *
     * @param id the user ID for which the token is required
     * @return a Mono emitting the JWT token as a String
     */
    public Mono<String> getToken(String id) {
        if (cachedToken == null || Instant.now().isAfter(expiresAt)) {
            return refreshToken(id);
        }
        return Mono.just(cachedToken);
        }

    /**
     * Calls the external user service to fetch user credentials and then requests a new JWT token.
     * Caches the token and sets its expiration time for future use.
     *
     * @param id the user ID for which to refresh the token
     * @return a Mono emitting the new JWT token
     */
    private Mono<String> refreshToken(String id) {
        return webClient.get()
        .uri(Url + "/user-service/users/by-number-id/" + id)
        .retrieve()
        .bodyToMono(User.class)
        .flatMap(user -> {
            if (user == null || user.userName() == null || user.password() == null) {
                return Mono.error(new IllegalStateException("Credenciales faltantes para usuario " + id));
            }

            Map<String, String> loginRequest = Map.of(
                "userName", user.userName(),
                "password", user.password()
            );

            return webClient.post()
                .uri(Url + "/user-service/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(String.class) 
                .doOnNext(body -> System.out.println("Respuesta cruda del login: " + body))
                .flatMap(body -> {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(body);
                    String token = rootNode.path("token").asText();
                    if (token == null || token.isEmpty()) {
                        return Mono.error(new IllegalStateException("Token es null o vacío"));
                    }
                    
                    this.cachedToken = token;
                    this.expiresAt = Instant.now().plus(Duration.ofMinutes(55));
                    return Mono.just(token);
                } catch (Exception e) {
                    return Mono.error(e);
                }
            });
        });
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public String getCachedToken() {
        return cachedToken;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public String getUrl() {
        return Url;
    }

    
}

