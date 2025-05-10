package kz.molten.techshop.orderservice.infrastructure.external;

import jakarta.annotation.Resource;
import kz.molten.techshop.orderservice.infrastructure.security.dto.JwtCredentialsDTO;
import kz.molten.techshop.orderservice.infrastructure.security.dto.JwtRefreshTokenDTO;
import kz.molten.techshop.orderservice.infrastructure.kafka.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClientImpl implements UserServiceClient {

    @Resource(name = "userServiceWebClient")
    private WebClient userServiceWebClient;

    @Override
    public TokenResponse getAccessAndRefreshToken(String username, String password) {
        log.info("Requesting new JWT from user-service for username: {}", username);

        return userServiceWebClient.post()
                .uri("/auth/login")
                .bodyValue(new JwtCredentialsDTO(username, password))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
    }

    @Override
    public TokenResponse refreshAccessToken(String refreshToken) {
        log.info("Sending request for refresh JWT from user-service");

        return userServiceWebClient.post()
                .uri("/auth/refresh-token")
                .bodyValue(new JwtRefreshTokenDTO(refreshToken))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
    }
}
