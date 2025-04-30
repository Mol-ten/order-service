package kz.molten.techshop.orderservice.infrastructure.external;

import kz.molten.techshop.orderservice.api.dto.response.TokenResponse;

public interface UserServiceClient {
    TokenResponse getAccessAndRefreshToken(String username, String password);
    TokenResponse refreshAccessToken(String refreshToken);
}
