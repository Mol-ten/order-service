package kz.molten.techshop.orderservice.application.mapper;

import kz.molten.techshop.orderservice.domain.event.OrderStatusChangedEvent;
import kz.molten.techshop.orderservice.domain.model.Order;
import kz.molten.techshop.orderservice.domain.model.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public class OrderEventMapper {
    public static OrderStatusChangedEvent toCreatedEvent(Order order, UUID eventId) {
        return OrderStatusChangedEvent.builder()
                .orderId(order.getId())
                .eventId(eventId)
                .newStatus(OrderStatus.ORDER_CREATED)
                .createdAt(order.getCreatedAt())
                .executedAt(Instant.now())
                .details("")
                .performedBy(2L)
                .build();
    }
}
