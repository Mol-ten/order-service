package kz.molten.techshop.orderservice.application.mapper;

import kz.molten.techshop.orderservice.domain.event.OrderStatusChangedEvent;
import kz.molten.techshop.orderservice.domain.model.ExecutedOrderHistory;
import kz.molten.techshop.orderservice.infrastructure.kafka.dto.KafkaOrderEventDTO;
import kz.molten.techshop.orderservice.api.dto.request.OrderHistoryDTO;
import kz.molten.techshop.orderservice.domain.model.Order;
import kz.molten.techshop.orderservice.domain.model.OrderHistory;

import java.util.UUID;

public class OrderHistoryMapper {
    public static OrderHistory fromDto(OrderHistoryDTO dto, Order order) {

        return OrderHistory.builder()
                .order(order)
                .orderStatus(dto.orderStatus())
                .eventId(dto.eventId())
                .executedAt(dto.executedAt())
                .details(dto.details())
                .performedBy(dto.performedBy())
                .build();
    }

    public static OrderHistory toDomain(Order order, OrderStatusChangedEvent event) {

        return OrderHistory.builder()
                .order(order)
                .orderStatus(event.getNewStatus())
                .eventId(event.getEventId())
                .executedAt(event.getExecutedAt())
                .details(event.getDetails())
                .performedBy(event.getPerformedBy())
                .build();
    }

    public static OrderHistoryDTO toDto(Order order, ExecutedOrderHistory executedDTO) {

        return OrderHistoryDTO.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .executedAt(executedDTO.getExecutedAt())
                .details(executedDTO.getDetails())
                .performedBy(executedDTO.getPerformedBy())
                .createdAt(order.getCreatedAt())
                .eventId(UUID.randomUUID())
                .build();
    }

    public static ExecutedOrderHistory toExecutedDto(KafkaOrderEventDTO eventDTO) {

        return ExecutedOrderHistory.builder()
                .executedAt(eventDTO.timestamp().toInstant())
                .details((String) eventDTO.metadata().getOrDefault("details", ""))
                .performedBy((Long) eventDTO.metadata().getOrDefault("performedBy", null))
                .build();
    }
}
