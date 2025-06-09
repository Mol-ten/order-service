package kz.molten.techshop.orderservice.infrastructure.kafka.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

public record KafkaPaymentEvent(
        @NotNull Long paymentId,
        @NotNull Long orderId,
        @NotNull @NotBlank String eventType,
        @NotNull UUID eventId,
        @NotNull Timestamp timestamp,
        Map<@NotNull String, @NotNull Object> metadata
) {
}
