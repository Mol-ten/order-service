package kz.molten.techshop.orderservice.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;

public record JwtRefreshTokenDTO(
        @NotBlank String refreshToken
) {
}
