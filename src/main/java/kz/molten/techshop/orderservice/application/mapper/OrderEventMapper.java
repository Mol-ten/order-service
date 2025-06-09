package kz.molten.techshop.orderservice.application.mapper;

import kz.molten.techshop.orderservice.domain.event.OrderStatusChangedEvent;
import kz.molten.techshop.orderservice.domain.event.OrderStatusChangedEvent.OrderStatusChangedEventBuilder;
import kz.molten.techshop.orderservice.domain.model.*;
import kz.molten.techshop.orderservice.domain.model.enumeration.OrderStatus;
import kz.molten.techshop.orderservice.domain.model.info.OrderCancellationInfo;
import kz.molten.techshop.orderservice.domain.model.info.OrderConfirmationInfo;
import kz.molten.techshop.orderservice.domain.model.info.OrderDeliveryInfo;
import kz.molten.techshop.orderservice.domain.model.info.OrderShippingInfo;

import java.time.Instant;
import java.util.UUID;

public class OrderEventMapper {
    private static final Long ORDER_SERVICE_USER_ID = 2L;

    public static OrderStatusChangedEvent toCreatedEvent(Order order, UUID eventId) {
         return createBaseBuilderFromOrder(order, eventId)
                 .newStatus(OrderStatus.CREATED)
                 .details("")
                 .performedBy(ORDER_SERVICE_USER_ID)
                 .build();
    }

    public static OrderStatusChangedEvent toConfirmedEvent(Order order, UUID eventId, OrderConfirmationInfo confirmationInfo) {
        return createBaseBuilderFromOrder(order, eventId)
                .newStatus(OrderStatus.CONFIRMED)
                .details(confirmationInfo.getConfirmationMessage())
                .performedBy(confirmationInfo.getManagerUserId())
                .build();
    }

    public static OrderStatusChangedEvent toShippedEvent(Order order, UUID eventId, OrderShippingInfo shippingInfo) {
        return createBaseBuilderFromOrder(order, eventId)
                .newStatus(OrderStatus.SHIPPED)
                .performedBy(shippingInfo.getCourierId())
                .details(shippingInfo.getMessage())
                .build();
    }

    public static OrderStatusChangedEvent toDeliveredEvent(Order order, UUID eventId, OrderDeliveryInfo deliveryInfo) {
        return createBaseBuilderFromOrder(order, eventId)
                .newStatus(OrderStatus.DELIVERED)
                .performedBy(0L)
                .details("")
                .build();
    }

    public static OrderStatusChangedEvent toCancelledEvent(Order order, UUID eventId, OrderCancellationInfo cancellationInfo) {
        return createBaseBuilderFromOrder(order, eventId)
                .newStatus(OrderStatus.CANCELLED)
                .performedBy(cancellationInfo.getSourceUserId())
                .details(cancellationInfo.getCancellationMessage())
                .build();
    }

    private static OrderStatusChangedEventBuilder createBaseBuilderFromOrder(Order order, UUID eventId) {
        return OrderStatusChangedEvent.builder()
                .orderId(order.getId())
                .eventId(eventId)
                .createdAt(Instant.now())
                .executedAt(Instant.now());
    }
}
