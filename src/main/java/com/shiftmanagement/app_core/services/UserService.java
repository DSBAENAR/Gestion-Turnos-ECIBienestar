package com.shiftmanagement.app_core.services;



import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shiftmanagement.app_core.model.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class UserService {
   private final WebClient webClient;
   private final JwtWebClientService jwtWebClientService;
   
   @Value("${api.auth.url}")
   private String uri;

   public UserService(WebClient webClient, JwtWebClientService jwtWebClientService) {
    this.webClient = webClient;
    this.jwtWebClientService = jwtWebClientService;
   }

    public Mono<User> getUserbyId(String id) {
        return jwtWebClientService.getToken(id)
            .flatMap(token -> webClient.get()
                .uri(uri + "/user-service/users/" + id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(byte[].class)  // Leemos como bytes
                .flatMap(bytes -> {
                    try {
                        String jsonResponse = new String(bytes, StandardCharsets.UTF_8); // Convertimos los bytes a String
                        ObjectMapper objectMapper = new ObjectMapper();
                        User user = objectMapper.readValue(jsonResponse, User.class);  // Parseamos el JSON
                        return Mono.just(user);
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);  // Manejo de error si no se puede parsear el JSON
                    }
                }));
    }


    public Flux<User> getUsers(String requesterId) {
    Mono<String> token = jwtWebClientService.getToken(requesterId);

    return webClient.get()
        .uri(uri + "/user-service/users")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .retrieve()
        .bodyToFlux(User.class);  
}

   
}
