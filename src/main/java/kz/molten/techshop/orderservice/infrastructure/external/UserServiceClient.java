package kz.molten.techshop.orderservice.infrastructure.external;

import kz.molten.techshop.orderservice.infrastructure.kafka.dto.TokenResponse;

public interface UserServiceClient {
    TokenResponse getAccessAndRefreshToken(String username, String password);
    TokenResponse refreshAccessToken(String refreshToken);
}
