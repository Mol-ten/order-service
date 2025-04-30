package kz.molten.techshop.orderservice.application.service;

import kz.molten.techshop.orderservice.domain.model.OrderStatus;
import kz.molten.techshop.orderservice.api.exception.OrderStatusTransitionException;
import kz.molten.techshop.orderservice.domain.repository.OrderRepository;
import kz.molten.techshop.orderservice.infrastructure.kafka.producer.KafkaOrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

import static kz.molten.techshop.orderservice.domain.model.OrderStatus.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusChanger {
    private final OrderRepository orderRepository;
    private final OrderHistoryService orderHistoryService;
    private final KafkaOrderEventPublisher orderEventPublisher;

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

//    @Transactional
//    public void changeStatus(KafkaOrderEvent event) {
//        Long orderId = event.getOrderId();
//
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));
//
//        OrderStatus currentStatus = order.getOrderStatus();
//        OrderStatus newStatus = event.getNewStatus();
//
//        if (currentStatus.equals(newStatus)) {
//            return;
//        }
//
//        validateTransition(currentStatus, newStatus);
//
//        switch (newStatus) {
//            case ORDER_CONFIRMED -> toConfirmation(order);
//        }
//
//        orderEventPublisher.publish();
//    }

//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @TransactionalEventListener(classes = {OrderStatusChangedEvent.class})
//    public void toConfirmation(OrderStatusChangedEvent event) {
//        Order order = orderRepository.findById(event.getOrderId())
//                .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));
//
//        ExecutedOrderHistory executedOrderHistoryDTO = ExecutedOrderHistory.builder()
//                .executedAt(Instant.now())
//                .performedBy((long) 6)
//                .details("")
//                .build();
//
//        OrderHistoryDTO orderHistoryDTO = OrderHistoryMapper.toDto(order, executedOrderHistoryDTO);
//
//        orderHistoryService.saveOrderHistory(orderHistoryDTO);
//        order.setOrderStatus(ORDER_CONFIRMED);
//
//        orderEventPublisher.publish(new KafkaOrderEventDTO());
//    }

    private static void validateTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        log.info("Checking for order status transition validity. Current status: {}, new status: {}", currentStatus, newStatus);

        if (!ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of()).contains(newStatus)) {
            log.error("Not valid order status transition. Current status: {}, trying to transit to: {}", currentStatus, newStatus);
            throw new OrderStatusTransitionException("Not valid order status transition. Current status: %s, trying to transit to: %s"
                    .formatted(currentStatus, newStatus));
        }
    }


}
