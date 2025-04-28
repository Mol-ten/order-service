package kz.molten.techshop.orderservice.dto;

import jakarta.validation.constraints.NotBlank;

public record JwtCredentialsDTO(
        @NotBlank String username,
        @NotBlank String password
) {
}
