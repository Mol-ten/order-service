package kz.molten.techshop.orderservice.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OrderCancellationDTO(
        @NotBlank String cancellationReason,
        @NotBlank String cancellationMessage
) {
}
