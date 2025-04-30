package kz.molten.techshop.orderservice.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;

public record JwtCredentialsDTO(
        @NotBlank String username,
        @NotBlank String password
) {
}
