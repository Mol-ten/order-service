package kz.molten.techshop.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kz.molten.techshop.orderservice.enumeration.OrderStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record OrderHistoryDTO(
        @NotNull @Min(1) Long orderId,
        @NotNull OrderStatus orderStatus,
        UUID eventId,
        @NotNull Instant createdAt,
        @NotNull Instant executedAt,
        String details,
        @Min(0) Long performedBy
        ) {
}
