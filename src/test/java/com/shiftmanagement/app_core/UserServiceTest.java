package com.shiftmanagement.app_core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shiftmanagement.app_core.model.User;
import com.shiftmanagement.app_core.services.JwtWebClientService;
import com.shiftmanagement.app_core.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private WebClient webClient;
    private JwtWebClientService jwtWebClientService;
    private UserService userService;

    // mocks de la cadena WebClient
    private WebClient.RequestHeadersUriSpec<?> headersUriSpec;
    private WebClient.RequestHeadersSpec<?> headersSpec;
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void setUp() throws Exception {
        webClient = mock(WebClient.class);
        jwtWebClientService = mock(JwtWebClientService.class);
        userService = new UserService(webClient, jwtWebClientService);

        // set uri privada
        Field uriField = UserService.class.getDeclaredField("uri");
        uriField.setAccessible(true);
        uriField.set(userService, userService.getUri());

        headersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        headersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        // chain WebClient with safe casts
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) headersUriSpec);
        when(((WebClient.RequestHeadersUriSpec) headersUriSpec).uri(anyString()))
            .thenReturn((WebClient.RequestHeadersSpec) headersSpec);
        when(((WebClient.RequestHeadersSpec) headersSpec)
            .header(eq(HttpHeaders.AUTHORIZATION), anyString()))
            .thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
    }

    
    @Test
    void getUserbyId_shouldReturnParsedUser() throws Exception {
        User mockUser = new User("John Doe", "123", "DOCTOR",null);
        byte[] responseJson = new ObjectMapper().writeValueAsBytes(mockUser);

        when(jwtWebClientService.getToken("123")).thenReturn(Mono.just("fake-token"));
        when(responseSpec.bodyToMono(byte[].class)).thenReturn(Mono.just(responseJson));

        StepVerifier.create(userService.getUserbyId("123"))
            .expectNextMatches(user ->
                user.numberId().equals("123") &&
                user.userName().equals("John Doe") &&
                user.role().equals("DOCTOR"))
            .verifyComplete();
    }

    @Test
void getUsers_shouldReturnListOfUsers() {
    // Arrange
    String requesterId = "admin";
    String token = "mockedToken";

    User user1 = new User("111", "Alice", "ASSISTANT", null);
    User user2 = new User("222", "Bob", "DOCTOR", null);

    when(jwtWebClientService.getToken(requesterId)).thenReturn(Mono.just(token));
    when(responseSpec.bodyToFlux(User.class)).thenReturn(Flux.just(user1, user2));

    // Act & Assert
    StepVerifier.create(userService.getUsers(requesterId))
        .expectNext(user1)
        .expectNext(user2)
        .verifyComplete();
    }
}
