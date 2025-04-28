package kz.molten.techshop.orderservice.domain.mapper;

import kz.molten.techshop.orderservice.domain.event.OrderCreatedEvent;
import kz.molten.techshop.orderservice.dto.ExecutedOrderHistoryDTO;
import kz.molten.techshop.orderservice.entity.Order;

public class OrderEventMapper {
    public static OrderCreatedEvent toCreatedEvent(Order order, ExecutedOrderHistoryDTO executedOrderHistoryDTO) {
        return OrderCreatedEvent.builder()
                .orderId(order.getId())
                .customerUserId(order.getCustomerUserId())
                .products(order.getProducts())
                .totalPrice(order.getTotalPrice())
                .provider(order.getProvider())
                .createdAt(order.getCreatedAt())
                .executedAt(executedOrderHistoryDTO.executedAt())
                .details(executedOrderHistoryDTO.details())
                .performedBy(executedOrderHistoryDTO.performedBy())
                .build();
    }
}
