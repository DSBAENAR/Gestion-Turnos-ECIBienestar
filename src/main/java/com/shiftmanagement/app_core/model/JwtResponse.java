package com.shiftmanagement.app_core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JwtResponse {
    @JsonProperty("authenticated")
    private boolean authenticated;
    
    @JsonProperty("user")
    private User user;
    
    @JsonProperty("token")
    private String token;
    
    @JsonProperty("message")
    private String message;

    // Constructor vacío (necesario para Jackson)
    public JwtResponse() {}

    // Getters y Setters
    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
