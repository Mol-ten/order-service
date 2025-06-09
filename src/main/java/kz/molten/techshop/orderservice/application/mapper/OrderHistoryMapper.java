package kz.molten.techshop.orderservice.application.mapper;

import kz.molten.techshop.orderservice.api.dto.request.OrderHistoryDTO;
import kz.molten.techshop.orderservice.domain.event.OrderStatusChangedEvent;
import kz.molten.techshop.orderservice.domain.event.PaymentStatusChangedEvent;
import kz.molten.techshop.orderservice.domain.model.Order;
import kz.molten.techshop.orderservice.domain.model.OrderHistory;
import kz.molten.techshop.orderservice.domain.model.enumeration.OrderHistoryStep;
import kz.molten.techshop.orderservice.domain.model.enumeration.PaymentStatus;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaPaymentEvent;

public class OrderHistoryMapper {

    public static OrderHistory fromDto(OrderHistoryDTO dto, Order order) {

        return OrderHistory.builder()
                .order(order)
                .orderHistoryStep(dto.orderHistoryStep())
                .eventId(dto.eventId())
                .executedAt(dto.executedAt())
                .details(dto.details())
                .performedBy(dto.performedBy())
                .build();
    }

    public static OrderHistory toDomain(Order order, OrderStatusChangedEvent event) {
        OrderHistoryStep orderHistoryStep = OrderHistoryStepMapper.mapFromOrderStatus(event.getNewStatus())
                .orElseThrow(() -> new IllegalArgumentException("Can't map %s status to OrderHistoryStep".formatted(event.getNewStatus())));

        return OrderHistory.builder()
                .order(order)
                .orderHistoryStep(orderHistoryStep)
                .eventId(event.getEventId())
                .executedAt(event.getExecutedAt())
                .details(event.getDetails())
                .performedBy(event.getPerformedBy())
                .build();
    }

    public static OrderHistory toDomain(Order order, KafkaPaymentEvent event) {
        OrderHistoryStep orderHistoryStep = OrderHistoryStepMapper.mapFromPaymentStatus(PaymentStatus.valueOf(event.eventType()))
                .orElseThrow(() -> new IllegalArgumentException("Can't map %s status to OrderHistoryStep".formatted(event.eventType())));

        return OrderHistory.builder()
                .order(order)
                .orderHistoryStep(orderHistoryStep)
                .eventId(event.eventId())
                .executedAt(event.timestamp().toInstant())
                .performedBy(3L)
                .build();
    }
}
