package kz.molten.techshop.orderservice.dto;

import jakarta.validation.constraints.NotBlank;

public record JwtRefreshTokenDTO(
        @NotBlank String refreshToken

) {
}
