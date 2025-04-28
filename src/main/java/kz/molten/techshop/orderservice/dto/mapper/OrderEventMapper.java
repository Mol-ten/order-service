package kz.molten.techshop.orderservice.dto.mapper;

import kz.molten.techshop.orderservice.dto.OrderEventDTO;
import kz.molten.techshop.orderservice.entity.Order;
import kz.molten.techshop.orderservice.entity.OrderStatusChangeEvent;
import kz.molten.techshop.orderservice.enumeration.OrderStatus;

import java.util.Map;
import java.util.UUID;

public class OrderEventMapper {
    public static OrderStatusChangeEvent toDomain(OrderEventDTO dto) {
        return OrderStatusChangeEvent.builder()
                .orderId(dto.orderId())
                .newStatus(OrderStatus.valueOf(dto.eventType()))
                .customerEmail(dto.customerEmail())
                .customerUserId(dto.customerUserId())
                .timestamp(dto.timestamp().toInstant())
                .metadata(dto.metadata())
                .build();
    }

    public static OrderEventDTO toDto(Order order, Map<String, Object> metadata) {
        return OrderEventDTO.builder()
                .eventId(UUID.randomUUID())
                .
                .build();
    }
}
