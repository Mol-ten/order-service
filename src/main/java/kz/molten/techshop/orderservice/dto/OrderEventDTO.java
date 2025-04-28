package kz.molten.techshop.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Builder
public record OrderEventDTO(
        @NotNull @Min(1) Long orderId,
        @NotBlank String eventType,
        @NotNull UUID eventId,
        @NotNull @Min(0) Long customerUserId,
        @NotBlank String customerEmail,
        @NotNull Timestamp timestamp,
        Map<@NotNull String, @NotNull Object> metadata
        ) {
}
