package kz.molten.techshop.orderservice.infrastructure.kafka.mapper;

import kz.molten.techshop.orderservice.domain.model.Order;
import kz.molten.techshop.orderservice.domain.model.OrderStatus;
import kz.molten.techshop.orderservice.infrastructure.kafka.dto.KafkaOrderEventDTO;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaOrderEvent;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KafkaOrderEventMapper {

    public static KafkaOrderEvent toDomain(KafkaOrderEventDTO dto) {
        return KafkaOrderEvent.builder()
                .orderId(dto.orderId())
                .orderStatus(OrderStatus.valueOf(dto.eventType()))
                .eventId(dto.eventId())
                .eventTimestamp(dto.timestamp())
                .metadata(dto.metadata())
                .build();
    }

    public static KafkaOrderEvent toCreatedEvent(Order order, UUID eventId) {
        return KafkaOrderEvent.builder()
                .orderId(order.getId())
                .orderStatus(OrderStatus.ORDER_CREATED)
                .eventId(eventId)
                .eventTimestamp(Timestamp.from(Instant.now()))
                .metadata(Map.of(
                        "customerUserId", order.getCustomerUserId(),
                        "provider", order.getProvider(),
                        "products", order.getProducts(),
                        "totalPrice", order.getTotalPrice()
                ))
                .build();
    }

    public static KafkaOrderEventDTO toDto(KafkaOrderEvent event) {
        return KafkaOrderEventDTO.builder()
                .orderId(event.getOrderId())
                .eventType(event.getOrderStatus().toString())
                .eventId(event.getEventId())
                .timestamp(event.getEventTimestamp())
                .metadata(event.getMetadata())
                .build();
    }
}
