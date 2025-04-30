package kz.molten.techshop.orderservice.api.dto.response;

import jakarta.validation.constraints.NotBlank;

public record TokenResponse(
        @NotBlank String token,
        @NotBlank String refreshToken
) {
}
