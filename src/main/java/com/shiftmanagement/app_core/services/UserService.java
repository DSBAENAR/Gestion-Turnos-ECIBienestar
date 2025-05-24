package com.shiftmanagement.app_core.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.shiftmanagement.app_core.model.User;

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

    public User getUserbyId(String id) {
        String token = jwtWebClientService.getToken();

        User user = webClient.get()
            .uri(uri + "/user-service/users/" + id)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .retrieve()
            .bodyToMono(User.class)
            .block();

        return user;
    }
   
}
