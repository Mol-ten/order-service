package kz.molten.techshop.orderservice.api.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record OrderShippingDTO(
        @NotNull @FutureOrPresent LocalDateTime requestedShippingTime,
        @NotBlank String message,
        @NotNull Long courierId
) {
}
