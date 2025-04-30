package kz.molten.techshop.orderservice.application.service;

import kz.molten.techshop.orderservice.api.dto.request.OrderCreateRequestDTO;
import kz.molten.techshop.orderservice.api.exception.OrderNotFoundException;
import kz.molten.techshop.orderservice.application.mapper.OrderEventMapper;
import kz.molten.techshop.orderservice.application.mapper.OrderProductMapper;
import kz.molten.techshop.orderservice.domain.model.Order;
import kz.molten.techshop.orderservice.domain.model.OrderProduct;
import kz.molten.techshop.orderservice.domain.model.OrderStatus;
import kz.molten.techshop.orderservice.domain.model.ProductInfo;
import kz.molten.techshop.orderservice.domain.repository.OrderRepository;
import kz.molten.techshop.orderservice.infrastructure.external.CatalogServiceClient;
import kz.molten.techshop.orderservice.infrastructure.kafka.mapper.KafkaOrderEventMapper;
import kz.molten.techshop.orderservice.infrastructure.kafka.producer.KafkaOrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;
    private final CatalogServiceClient catalogServiceClient;
    private final KafkaOrderEventPublisher orderEventPublisher;

    public Order getOrderById(Long id) {
        log.info("Fetching Order by id: {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<Order> getOrdersByUserId(Long userId) {
        log.info("Fetching all order of user with id: {}", userId);
        List<Order> orders = orderRepository.findAllByCustomerUserId(userId);
        log.debug("{} order were found of user with id: {}", orders.size(), userId);

        return orders;
    }

    @Transactional
    public Order createOrder(Long userId, OrderCreateRequestDTO dto) {
        log.info("Creating order of user with id: {}. OrderCreateRequestDTO: {}", userId, dto);

        Order order = new Order();
        order.setCustomerUserId(userId);
        order.setOrderStatus(OrderStatus.ORDER_CREATED);
        order.setProvider(dto.provider());

        List<OrderProduct> orderProductList = dto.products().stream()
                .map(OrderProductMapper::fromDto)
                .toList();

        reserveOrderProducts(orderProductList);
        orderProductList.forEach(order::addProduct);
        order.calculateTotalPrice();

        orderRepository.save(order);

        UUID eventId = UUID.randomUUID();
        eventPublisher.publishEvent(OrderEventMapper.toCreatedEvent(order, eventId));
        orderEventPublisher.publish(KafkaOrderEventMapper.toCreatedEvent(order, eventId));

        return order;
    }

    private void reserveOrderProducts(List<OrderProduct> orderProductList) {
        log.info("Reserving OrderProducts from CatalogService");
        orderProductList.forEach(orderProduct -> log.info(orderProduct.toString()));

        Map<Long, Integer> productsMap = orderProductList.stream()
                .collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity));

        Map<Long, ProductInfo> productInfoMap = catalogServiceClient.reserveProducts(productsMap).stream()
                .collect(Collectors.toMap(ProductInfo::getId, Function.identity()));

        for (OrderProduct orderProduct : orderProductList) {
            ProductInfo productInfo = productInfoMap.get(orderProduct.getProductId());
            orderProduct.setFixedPrice(productInfo.getPrice());
        }
    }
}
