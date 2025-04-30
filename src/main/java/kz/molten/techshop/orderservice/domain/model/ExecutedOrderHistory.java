package kz.molten.techshop.orderservice.domain.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Builder
@ToString
@Getter
public class ExecutedOrderHistory {
    private final Instant executedAt;
    private final String details;
    private final Long performedBy;
}
