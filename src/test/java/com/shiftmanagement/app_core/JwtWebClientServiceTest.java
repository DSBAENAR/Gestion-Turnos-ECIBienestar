package com.shiftmanagement.app_core;
import com.shiftmanagement.app_core.model.User;
import com.shiftmanagement.app_core.services.JwtWebClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Map;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;






public class JwtWebClientServiceTest {

    private WebClient webClient;
    private JwtWebClientService jwtWebClientService;

    @BeforeEach
    void setUp() throws Exception {
        webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        jwtWebClientService = new JwtWebClientService(webClient);

        // Set Url field via reflection
        Field urlField = JwtWebClientService.class.getDeclaredField("Url");
        urlField.setAccessible(true);
        urlField.set(jwtWebClientService, "http://localhost");
    }

    @Test
    void testGetToken_ReturnsCachedToken() throws Exception {
        Field tokenField = JwtWebClientService.class.getDeclaredField("cachedToken");
        tokenField.setAccessible(true);
        tokenField.set(jwtWebClientService, "token123");
        Field expiresAtField = JwtWebClientService.class.getDeclaredField("expiresAt");
        expiresAtField.setAccessible(true);
        expiresAtField.set(jwtWebClientService, Instant.now().plusSeconds(60));

        StepVerifier.create(jwtWebClientService.getToken("1"))
                .expectNext("token123")
                .verifyComplete();
    }

    @Test
    void testGetToken_RefreshesToken() {
        // Mock user fetch
        WebClient.RequestHeadersUriSpec getSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec getHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec getResponseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(getSpec);
        when(getSpec.uri(anyString())).thenReturn(getHeadersSpec);
        when(getHeadersSpec.retrieve()).thenReturn(getResponseSpec);
        when(getResponseSpec.bodyToMono(User.class)).thenReturn(Mono.just(new User("user", "123", "ADMIN", "pass")));
        // Adjusted for record constructor: userName, numberId, role, password
        when(getResponseSpec.bodyToMono(User.class))
            .thenReturn(Mono.just(new User("user", "123", "ADMIN", "pass")));
        // Mock login fetch
        WebClient.RequestBodyUriSpec postSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec<?> postHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec postResponseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(postSpec);
        when(postSpec.uri(anyString())).thenReturn(postSpec);
        when(postSpec.header(eq(HttpHeaders.CONTENT_TYPE), eq(MediaType.APPLICATION_JSON_VALUE))).thenReturn(postSpec);
        when(postSpec.bodyValue(any(Map.class))).thenReturn((WebClient.RequestHeadersSpec) postHeadersSpec);
        when(postHeadersSpec.retrieve()).thenReturn(postResponseSpec);

        String tokenJson = "{\"token\":\"jwt-token-value\"}";
        when(postResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just(tokenJson));

        StepVerifier.create(jwtWebClientService.getToken("123"))
                .expectNext("jwt-token-value")
                .verifyComplete();
    }

    @Test
    void testGetToken_UserMissingCredentials() {
        WebClient.RequestHeadersUriSpec getSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec getHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec getResponseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(getSpec);
        when(getSpec.uri(anyString())).thenReturn(getHeadersSpec);
        when(getHeadersSpec.retrieve()).thenReturn(getResponseSpec);
        when(getResponseSpec.bodyToMono(User.class)).thenReturn(Mono.just(new User(null, null, null, null)));

        StepVerifier.create(jwtWebClientService.getToken("123"))
                .expectError(IllegalStateException.class)
                .verify();
    }
}