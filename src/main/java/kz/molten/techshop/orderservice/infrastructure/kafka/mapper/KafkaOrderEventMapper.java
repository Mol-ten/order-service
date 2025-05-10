package kz.molten.techshop.orderservice.infrastructure.kafka.mapper;

import kz.molten.techshop.orderservice.domain.model.*;
import kz.molten.techshop.orderservice.infrastructure.kafka.dto.KafkaOrderEventDTO;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaOrderEvent;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaOrderEvent.KafkaOrderEventBuilder;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static kz.molten.techshop.orderservice.domain.model.OrderStatus.*;

public class KafkaOrderEventMapper {

    public static KafkaOrderEvent toDomain(KafkaOrderEventDTO dto) {
        return KafkaOrderEvent.builder()
                .orderId(dto.orderId())
                .eventType(dto.eventType())
                .eventId(dto.eventId())
                .eventTimestamp(dto.timestamp())
                .metadata(dto.metadata())
                .build();
    }

    public static KafkaOrderEvent toCreatedEvent(Order order, UUID eventId) {
        return getBaseBuilderFromOrder(order, eventId)
                .eventType(CREATED.toString())
                .metadata(Map.of(
                        "customerUserId", order.getCustomerUserId(),
                        "provider", order.getProvider(),
                        "products", order.getProducts(),
                        "totalPrice", order.getTotalPrice()
                ))
                .build();
    }

    public static KafkaOrderEvent toConfirmedEvent(Order order, UUID eventId, OrderConfirmationInfo confirmationInfo) {
        return getBaseBuilderFromOrder(order, eventId)
                .eventType(CONFIRMED.toString())
                .metadata(Map.of(
                        "managerUserId", confirmationInfo.getManagerUserId(),
                        "confirmationMessage", confirmationInfo.getConfirmationMessage(),
                        "confirmationSource", confirmationInfo.getConfirmationSource()
                ))
                .build();
    }

    public static KafkaOrderEvent toShippedEvent(Order order, UUID eventId, OrderShippingInfo shippingInfo) {
        return getBaseBuilderFromOrder(order, eventId)
                .eventType(SHIPPED.toString())
                .metadata(Map.of(
                        "courierId", shippingInfo.getCourierId(),
                        "message", shippingInfo.getMessage(),
                        "address", shippingInfo.getAddress(),
                        "shippingTime", shippingInfo.getShippingTime()
                ))
                .build();
    }

    public static KafkaOrderEvent toDeliveredEvent(Order order, UUID eventId, OrderDeliveryInfo deliveryInfo) {
        return getBaseBuilderFromOrder(order, eventId)
                .eventType(DELIVERED.toString())
                .metadata(Map.of(
                        "customerUserId", deliveryInfo.getCustomerUserId(),
                        "courierId", deliveryInfo.getCourierId(),
                        "deliveryDateTime", deliveryInfo.getDeliveryDateTime()
                ))
                .build();
    }

    public static KafkaOrderEvent toCancelledEvent(Order order, UUID eventId, OrderCancellationInfo cancellationInfo) {
        return getBaseBuilderFromOrder(order, eventId)
                .eventType(CANCELLED.toString())
                .metadata(Map.of(
                        "sourceUserId", cancellationInfo.getSourceUserId(),
                        "cancellationReason", cancellationInfo.getCancellationReason(),
                        "cancellationMessage", cancellationInfo.getCancellationMessage()
                ))
                .build();
    }

    public static KafkaOrderEventDTO toDto(KafkaOrderEvent event) {
        return KafkaOrderEventDTO.builder()
                .orderId(event.getOrderId())
                .eventType(event.getEventType())
                .eventId(event.getEventId())
                .timestamp(event.getEventTimestamp())
                .metadata(event.getMetadata())
                .build();
    }

    private static KafkaOrderEventBuilder getBaseBuilderFromOrder(Order order, UUID eventId) {
        return KafkaOrderEvent.builder()
                .orderId(order.getId())
                .eventId(eventId)
                .eventTimestamp(Timestamp.from(Instant.now()));
    }
}
