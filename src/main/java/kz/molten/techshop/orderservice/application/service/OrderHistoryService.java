package kz.molten.techshop.orderservice.application.service;

import kz.molten.techshop.orderservice.domain.event.OrderStatusChangedEvent;
import kz.molten.techshop.orderservice.domain.event.PaymentStatusChangedEvent;
import kz.molten.techshop.orderservice.domain.model.*;
import kz.molten.techshop.orderservice.api.dto.request.OrderHistoryDTO;
import kz.molten.techshop.orderservice.application.mapper.OrderHistoryMapper;
import kz.molten.techshop.orderservice.api.exception.OrderHistoryNotFoundException;
import kz.molten.techshop.orderservice.api.exception.OrderNotFoundException;
import kz.molten.techshop.orderservice.domain.repository.OrderHistoryRepository;
import kz.molten.techshop.orderservice.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderRepository orderRepository;

    public OrderHistory getOrderHistory(Long id) {
        log.info("Fetching OrderHistory by id: {}", id);
        return orderHistoryRepository.findById(id)
                .orElseThrow(() -> new OrderHistoryNotFoundException(id));
    }

    public List<OrderHistory> getOrderHistoryByOrder(Long orderId) {
        log.info("Fetching OrderHistory by orderId: {}", orderId);
        List<OrderHistory> orderHistory = orderHistoryRepository.findAllByOrderIdOrderByCreatedAt(orderId);
        log.debug("{} record of OrderHistory with orderId: {} was found", orderHistory.size(), orderId);
        return orderHistory;
    }

    public boolean existsByEventId(UUID eventId) {
        log.info("Checking if OrderHistory exists by EventId: {}", eventId);

        return orderHistoryRepository.existsByEventId(eventId);
    }

    @Transactional
    public OrderHistory saveOrderHistory(OrderHistoryDTO dto) {
        log.info("Saving orderHistory for order with id: {} and status: {}", dto.orderId(), dto.orderHistoryStep());

        final Order order = orderRepository.findById(dto.orderId())
                .orElseThrow(() -> new OrderNotFoundException(dto.orderId()));

        OrderHistory orderHistory = OrderHistoryMapper.fromDto(dto, order);

        orderHistoryRepository.saveAndFlush(orderHistory);
        log.info("orderHistory for order with id: {} and status: {} was saved", dto.orderId(), dto.orderHistoryStep());

        return orderHistory;
    }

    @EventListener(OrderStatusChangedEvent.class)
    public void saveOrderHistory(OrderStatusChangedEvent event) {
        log.info("Saving orderHistory for order with id: {} and status: {}", event.getOrderId(), event.getNewStatus());

        final Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));

        OrderHistory orderHistory = OrderHistoryMapper.toDomain(order, event);

        orderHistoryRepository.save(orderHistory);

        log.info("OrderHistory for order with id: {} and status: {} was saved", event.getOrderId(), event.getNewStatus());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(PaymentStatusChangedEvent.class)
    @org.springframework.core.annotation.Order(2)
    public void saveOrderHistory(PaymentStatusChangedEvent event) {
        log.info("Saving payment OrderHistory for order with id: {}", event.getOrderId());

        final Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(event.getOrderId()));

        OrderHistory orderHistory = OrderHistoryMapper.toDomain(order, event);

        orderHistoryRepository.save(orderHistory);
    }
}
