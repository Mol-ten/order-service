package kz.molten.techshop.orderservice.dto.mapper;

import kz.molten.techshop.orderservice.dto.ExecutedOrderHistoryDTO;
import kz.molten.techshop.orderservice.dto.OrderEventDTO;
import kz.molten.techshop.orderservice.dto.OrderHistoryDTO;
import kz.molten.techshop.orderservice.entity.Order;
import kz.molten.techshop.orderservice.entity.OrderHistory;
import kz.molten.techshop.orderservice.enumeration.OrderStatus;

import java.time.Instant;
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

    public static OrderHistoryDTO toDto(Order order, ExecutedOrderHistoryDTO executedDTO) {

        return OrderHistoryDTO.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .executedAt(executedDTO.executedAt())
                .details(executedDTO.details())
                .performedBy(executedDTO.performedBy())
                .createdAt(order.getCreatedAt())
                .eventId(UUID.randomUUID())
                .build();
    }

    public static OrderHistory fromOrder(Order order) {
        return OrderHistory.builder()
                .order(order)
                .orderStatus(order.getOrderStatus())
                .eventId(UUID.randomUUID())
                .createdAt(order.getCreatedAt())
                .build();
    }

    public static ExecutedOrderHistoryDTO toExecutedDto(OrderEventDTO eventDTO) {

        return ExecutedOrderHistoryDTO.builder()
                .executedAt(eventDTO.timestamp().toInstant())
                .details((String) eventDTO.metadata().getOrDefault("details", ""))
                .performedBy((Long) eventDTO.metadata().getOrDefault("performedBy", null))
                .build();
    }
}
