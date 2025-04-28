package kz.molten.techshop.orderservice.entity;

import kz.molten.techshop.orderservice.enumeration.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
public class OrderStatusChangeEvent {
    private Long orderId;
    private OrderStatus newStatus;
    private UUID eventId;
    private Long customerUserId;
    private String customerEmail;
    private Instant timestamp;
    Map<String, Object> metadata;
}
