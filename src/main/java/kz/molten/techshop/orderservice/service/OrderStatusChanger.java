package kz.molten.techshop.orderservice.service;

import kz.molten.techshop.orderservice.domain.event.OrderCreatedEvent;
import kz.molten.techshop.orderservice.dto.ExecutedOrderHistoryDTO;
import kz.molten.techshop.orderservice.dto.OrderEventDTO;
import kz.molten.techshop.orderservice.dto.OrderHistoryDTO;
import kz.molten.techshop.orderservice.dto.mapper.OrderEventMapper;
import kz.molten.techshop.orderservice.dto.mapper.OrderHistoryMapper;
import kz.molten.techshop.orderservice.entity.Order;
import kz.molten.techshop.orderservice.entity.OrderHistory;
import kz.molten.techshop.orderservice.entity.OrderStatusChangeEvent;
import kz.molten.techshop.orderservice.enumeration.OrderStatus;
import kz.molten.techshop.orderservice.exception.OrderNotFoundException;
import kz.molten.techshop.orderservice.exception.OrderStatusTransitionException;
import kz.molten.techshop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

import static kz.molten.techshop.orderservice.enumeration.OrderStatus.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusChanger {
    private final OrderRepository orderRepository;
    private final OrderHistoryService orderHistoryService;
    private final OrderEventPublisher orderEventPublisher;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS;

    static {
        Map<OrderStatus, Set<OrderStatus>> transitionMap = new HashMap<>();
        transitionMap.put(ORDER_CREATED, Set.of(ORDER_CONFIRMED, CANCELLED));
        transitionMap.put(ORDER_CONFIRMED, Set.of(PAYMENT_PENDING, CANCELLED));
        transitionMap.put(PAYMENT_PENDING, Set.of(PAYMENT_COMPLETED, PAYMENT_FAILED, PAYMENT_EXPIRED, CANCELLED));
        transitionMap.put(PAYMENT_COMPLETED, Set.of(DELIVERY, CANCELLED));
        transitionMap.put(PAYMENT_FAILED, Set.of(PAYMENT_PENDING, CANCELLED));
        transitionMap.put(PAYMENT_EXPIRED, Set.of(PAYMENT_PENDING, CANCELLED));
        transitionMap.put(DELIVERY, Set.of(DELIVERED, CANCELLED));
        transitionMap.put(DELIVERED, Set.of(CANCELLED));
        transitionMap.put(CANCELLED, Set.of());

        ALLOWED_TRANSITIONS = Collections.unmodifiableMap(transitionMap);
    }

    @Transactional
    public void changeStatus(OrderStatusChangeEvent event) {
        Long orderId = event.getOrderId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));

        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus newStatus = event.getNewStatus();

        if (currentStatus.equals(newStatus)) {
            return;
        }

        validateTransition(currentStatus, newStatus);

        switch (newStatus) {
            case ORDER_CONFIRMED -> toConfirmation(order);
        }

        orderEventPublisher.publish();
    }

    @Transactional
    @EventListener(classes = {OrderCreatedEvent.class)
    public void toConfirmation(OrderCreatedEvent event) {
        UUID eventId = UUID.randomUUID();


        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));

        ExecutedOrderHistoryDTO executedOrderHistoryDTO = ExecutedOrderHistoryDTO.builder()
                .executedAt(Instant.now())
                .performedBy((long) 6)
                .details("")
                .build();

        OrderHistoryDTO orderHistoryDTO = OrderHistoryMapper.toDto(order, executedOrderHistoryDTO);

        orderHistoryService.saveOrderHistory(orderHistoryDTO);
        order.setOrderStatus(ORDER_CONFIRMED);

        orderEventPublisher.publish(new OrderEventDTO());
    }

    private static void validateTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        log.info("Checking for order status transition validity. Current status: {}, new status: {}", currentStatus, newStatus);

        if (!ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of()).contains(newStatus)) {
            log.error("Not valid order status transition. Current status: {}, trying to transit to: {}", currentStatus, newStatus);
            throw new OrderStatusTransitionException("Not valid order status transition. Current status: %s, trying to transit to: %s"
                    .formatted(currentStatus, newStatus));
        }
    }


}
