package kz.molten.techshop.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ExecutedOrderHistoryDTO(
        @NotNull Instant executedAt,
        String details,
        Long performedBy
        ) {
}
