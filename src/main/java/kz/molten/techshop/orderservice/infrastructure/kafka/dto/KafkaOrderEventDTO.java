package kz.molten.techshop.orderservice.infrastructure.kafka.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Builder
public record KafkaOrderEventDTO(
        @NotNull @Min(1) Long orderId,
        @NotBlank String eventType,
        @NotNull UUID eventId,
        @NotNull Timestamp timestamp,
        Map<@NotNull String, @NotNull Object> metadata
) {
}
