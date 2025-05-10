package kz.molten.techshop.orderservice.application.service;

import kz.molten.techshop.orderservice.api.dto.request.OrderCreateRequestDTO;
import kz.molten.techshop.orderservice.api.exception.IllegalOrderStatusException;
import kz.molten.techshop.orderservice.api.exception.IllegalPaymentStatusException;
import kz.molten.techshop.orderservice.api.exception.OrderIllegalAccessException;
import kz.molten.techshop.orderservice.api.exception.OrderNotFoundException;
import kz.molten.techshop.orderservice.application.mapper.OrderProductMapper;
import kz.molten.techshop.orderservice.domain.event.PaymentStatusChangedEvent;
import kz.molten.techshop.orderservice.domain.model.*;
import kz.molten.techshop.orderservice.domain.repository.OrderRepository;
import kz.molten.techshop.orderservice.infrastructure.external.CatalogServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kz.molten.techshop.orderservice.domain.model.OrderStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Integer ORDER_EXPIRATION_HOURS = 3;

    private final OrderRepository orderRepository;
    private final CatalogServiceClient catalogServiceClient;
    private final OrderEventDispatcher orderEventDispatcher;

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
        order.setOrderStatus(CREATED);
        order.setProvider(dto.provider());
        order.setPaymentStatus(PaymentStatus.PAYMENT_PENDING);

        List<OrderProduct> orderProductList = dto.products().stream()
                .map(OrderProductMapper::fromDto)
                .toList();

        reserveOrderProducts(orderProductList);
        orderProductList.forEach(order::addProduct);
        order.calculateTotalPrice();

        orderRepository.save(order);

        orderEventDispatcher.publishCreated(order);

        return order;
    }

    @Transactional
    public void confirmOrder(Long id, OrderConfirmationInfo confirmationInfo) {
        log.info("Confirming order with id: {}", id);

        Order order = getOrderById(id);

        if (!order.isOrderStatusEquals(CREATED)) {
            throw new IllegalOrderStatusException("Order must be in \"CREATED\" status to confirm");
        }

        order.setOrderStatus(CONFIRMED);

        orderRepository.save(order);

        orderEventDispatcher.publishConfirmed(order, confirmationInfo);
    }

    @Transactional
    public void shipOrder(Long id) {
        log.info("Shipping order with id: {}", id);

        Order order = getOrderById(id);

        if (!order.isOrderPaid()) {
            throw new IllegalPaymentStatusException("PaymentStatus \"PAYMENT_COMPLETED\" is required to ship order");
        } else if (!order.isOrderStatusEquals(CONFIRMED)) {
            throw new IllegalOrderStatusException("OrderStatus \"CONFIRMED\" is required to ship order");
        }


        order.setOrderStatus(SHIPPED);

        orderRepository.save(order);

        OrderShippingInfo shippingInfo = OrderShippingInfo.builder()
                .address("Mock address")
                .shippingTime(LocalDateTime.of(2025, 5, 7, 18, 0))
                .message("Test")
                .courierId(0L)
                .build(); // TODO: fetch it from DB

        orderEventDispatcher.publishShipped(order, shippingInfo);
    }

    @Transactional
    public void deliverOrder(Long id, OrderDeliveryInfo deliveryInfo) {
        log.info("Setting Order with id: {} as delivered", id);

        Order order = getOrderById(id);

        if (!order.isOrderStatusEquals(SHIPPED)) {
            throw new IllegalOrderStatusException("OrderStatus \"SHIPPED\" is required to ship order");
        }

        order.setOrderStatus(DELIVERED);

        orderRepository.save(order);

        catalogServiceClient.releaseReserve(order.getProductsMap());

        orderEventDispatcher.publishDelivered(order, deliveryInfo);
    }

    @Transactional
    public void cancelOrder(Long id, OrderCancellationInfo cancellationInfo) {
        log.info("Trying to cancel order with id: {}", id);

        Order order = getOrderById(id);

        if (!Objects.equals(order.getCustomerUserId(), cancellationInfo.getSourceUserId())
                && !Objects.equals(cancellationInfo.getSourceUserId(), 2L)) {
            throw new OrderIllegalAccessException("There is no order with id: %d in your list of orders".formatted(id));
        }

        if (order.isOrderPaid()) {
            throw new IllegalPaymentStatusException("You can't cancel order that has already been paid");
        }

        order.setOrderStatus(CANCELLED);

        orderRepository.save(order);

        catalogServiceClient.revertReserve(order.getProductsMap());

        orderEventDispatcher.publishCancelled(order, cancellationInfo);
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cancelExpiredOrders() {
        log.info("Canceling expired orders");

        LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Asia/Almaty"));
        Instant cutoff = dateTime.toInstant(ZoneOffset.ofHours(1))
                .minusSeconds(ORDER_EXPIRATION_HOURS * 60 * 60);

        List<Order> expiredOrders = orderRepository.findAllByCreatedAtBeforeAndPaymentStatusNotAndOrderStatusNot(
                cutoff,
                PaymentStatus.PAYMENT_COMPLETED,
                CANCELLED
        );

        OrderCancellationInfo cancellationInfo = OrderCancellationInfo.builder()
                .sourceUserId(2L)
                .cancellationReason("EXPIRED WITHIN 3 HOURS")
                .cancellationMessage("SYSTEM AUTO CANCELLATION")
                .build();

        expiredOrders.forEach(order -> {
            log.info("Cancelling order with id : {} due to expiration", order.getId());
            cancelOrder(order.getId(), cancellationInfo);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(PaymentStatusChangedEvent.class)
    @org.springframework.core.annotation.Order(1)
    public void changePaymentStatus(PaymentStatusChangedEvent event) {
        log.info("Changing PaymentStatus of order with id: {}", event.getOrderId());

        Order order = getOrderById(event.getOrderId());
        order.setPaymentStatus(event.getPaymentStatus());
        orderRepository.save(order);
    }

    private void reserveOrderProducts(List<OrderProduct> orderProductList) {
        log.info("Reserving OrderProducts from CatalogService");

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
