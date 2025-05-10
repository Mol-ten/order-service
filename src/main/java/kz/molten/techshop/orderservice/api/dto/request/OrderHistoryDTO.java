package kz.molten.techshop.orderservice.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kz.molten.techshop.orderservice.domain.model.OrderHistoryStep;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record OrderHistoryDTO(
        @NotNull @Min(1) Long orderId,
        @NotNull OrderHistoryStep orderHistoryStep,
        UUID eventId,
        @NotNull Instant createdAt,
        @NotNull Instant executedAt,
        String details,
        @Min(0) Long performedBy
        ) {
}
