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

   /**
     * Retrieves a user by their unique identifier.
     * Makes an authenticated request to an external user service and parses the JSON response manually.
     *
     * @param id the user ID to look up
     * @return a Mono emitting the User object
     */
    public Mono<User> getUserbyId(String id) {
        return jwtWebClientService.getToken(id)
            .flatMap(token -> webClient.get()
                .uri(uri + "/user-service/users/by-number-id/" + id)
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

    /**
     * Retrieves all users from the external user service.
     * Requires a valid token obtained using the requester's ID.
     *
     * @param requesterId the ID of the user making the request, used to fetch the JWT token
     * @return a Flux emitting User objects
     */
    public Flux<User> getUsers(String requesterId) {
        Mono<String> token = jwtWebClientService.getToken(requesterId);

        return webClient.get()
            .uri(uri + "/user-service/users")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToFlux(User.class);  
        }

    public String getUri() {
        return uri;
    }
    

   
}
