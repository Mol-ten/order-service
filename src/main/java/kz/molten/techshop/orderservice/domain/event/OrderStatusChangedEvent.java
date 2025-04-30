package kz.molten.techshop.orderservice.domain.event;

import kz.molten.techshop.orderservice.domain.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@ToString
public class OrderStatusChangedEvent {
    private final Long orderId;
    private final OrderStatus newStatus;
    private final UUID eventId;
    private final Instant createdAt;
    private final Instant executedAt;
    private final String details;
    private final Long performedBy;
}
