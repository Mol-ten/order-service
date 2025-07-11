package kz.molten.techshop.orderservice.api.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CustomerDeliveryDTO(
        @NotNull Boolean isNewDeliveryInfo,
        @NotBlank String address,
        @NotNull @FutureOrPresent LocalDateTime deliveryTime
) {
}
