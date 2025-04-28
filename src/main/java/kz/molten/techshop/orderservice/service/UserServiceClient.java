package kz.molten.techshop.orderservice.service;

import kz.molten.techshop.orderservice.dto.TokenResponse;

public interface UserServiceClient {
    TokenResponse getAccessAndRefreshToken(String username, String password);
    TokenResponse refreshAccessToken(String refreshToken);
}
