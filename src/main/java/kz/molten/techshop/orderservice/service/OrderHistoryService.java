package kz.molten.techshop.orderservice.service;

import kz.molten.techshop.orderservice.dto.ExecutedOrderHistoryDTO;
import kz.molten.techshop.orderservice.dto.OrderHistoryDTO;
import kz.molten.techshop.orderservice.dto.mapper.OrderHistoryMapper;
import kz.molten.techshop.orderservice.entity.Order;
import kz.molten.techshop.orderservice.entity.OrderHistory;
import kz.molten.techshop.orderservice.exception.OrderHistoryNotFoundException;
import kz.molten.techshop.orderservice.exception.OrderNotFoundException;
import kz.molten.techshop.orderservice.repository.OrderHistoryRepository;
import kz.molten.techshop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public OrderHistory saveOrderHistory(OrderHistoryDTO dto) {
        log.info("Saving orderHistory for order with id: {} and status: {}", dto.orderId(), dto.orderStatus());

        Order order = orderRepository.findById(dto.orderId())
                .orElseThrow(() -> new OrderNotFoundException(dto.orderId()));

        OrderHistory orderHistory = OrderHistoryMapper.fromDto(dto, order);

        orderHistoryRepository.saveAndFlush(orderHistory);
        log.info("orderHistory for order with id: {} and status: {} was saved", dto.orderId(), dto.orderStatus());

        return orderHistory;
    }

    @Transactional
    public void saveExecutedOrderHistory(Long orderHistoryId, ExecutedOrderHistoryDTO dto) {
        log.info("Saving execute info of OrderHistory with id: {}", orderHistoryId);

        OrderHistory orderHistory = getOrderHistory(orderHistoryId);
        orderHistory.setExecutedAt(dto.executedAt());
        orderHistory.setDetails(dto.details());
        orderHistory.setPerformedBy(dto.performedBy());

        orderHistoryRepository.save(orderHistory);

        log.info("Execute info of OrderHistory with id: {} was saved", orderHistoryId);
    }
}
