package kz.molten.techshop.orderservice.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenResponse(
        @NotBlank String token,
        @NotBlank String refreshToken
) {
}
