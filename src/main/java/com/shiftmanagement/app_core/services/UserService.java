package com.shiftmanagement.app_core.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.shiftmanagement.app_core.model.User;

@Service
public class UserService {
   private final WebClient webClient;
   
   @Value("${api.auth.url}")
   private String uri;

   public UserService(WebClient webClient) {
    this.webClient = webClient;
   }

    public User getUserbyId(String id) {
        
        User user = webClient.get()
            .uri(uri + "/user-service/users/" + id)
            .retrieve()
            .bodyToMono(User.class)
            .block();
        
        return user;
    }

    public void getUsers() {

        User[] users = webClient.get()
            .uri(uri + "/user-service/users")
            .retrieve()
            .bodyToMono(User[].class)
            .block();

        for (User user:users){
            System.out.println(user.toString());
        }
    }
   
}
