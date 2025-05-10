package kz.molten.techshop.orderservice.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OrderConfirmationRequestDTO(
        @NotBlank String message,
        @NotBlank String confirmationSource
) {
}
