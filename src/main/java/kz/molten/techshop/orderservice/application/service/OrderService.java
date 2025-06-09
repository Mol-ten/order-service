package kz.molten.techshop.orderservice.application.service;

import kz.molten.techshop.orderservice.api.dto.request.CustomerDeliveryDTO;
import kz.molten.techshop.orderservice.api.dto.request.OrderCreateRequestDTO;
import kz.molten.techshop.orderservice.api.dto.request.OrderDeliveryDTO;
import kz.molten.techshop.orderservice.api.dto.request.OrderShippingDTO;
import kz.molten.techshop.orderservice.api.exception.OrderNotFoundException;
import kz.molten.techshop.orderservice.application.mapper.OrderDeliveryInfoMapper;
import kz.molten.techshop.orderservice.application.mapper.OrderProductMapper;
import kz.molten.techshop.orderservice.application.mapper.OrderShippingInfoMapper;
import kz.molten.techshop.orderservice.domain.event.PaymentStatusChangedEvent;
import kz.molten.techshop.orderservice.domain.model.CustomerDelivery;
import kz.molten.techshop.orderservice.domain.model.Order;
import kz.molten.techshop.orderservice.domain.model.OrderProduct;
import kz.molten.techshop.orderservice.domain.model.enumeration.OrderStatus;
import kz.molten.techshop.orderservice.domain.model.enumeration.PaymentStatus;
import kz.molten.techshop.orderservice.domain.model.enumeration.Provider;
import kz.molten.techshop.orderservice.domain.model.info.*;
import kz.molten.techshop.orderservice.domain.repository.OrderRepository;
import kz.molten.techshop.orderservice.infrastructure.external.CatalogServiceClient;
import kz.molten.techshop.orderservice.infrastructure.external.dto.ProductReservationDTO;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaPaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kz.molten.techshop.orderservice.domain.model.enumeration.OrderStatus.CANCELLED;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Integer ORDER_EXPIRATION_HOURS = 3;

    private final OrderHistoryService historyService;
    private final OrderRepository orderRepository;
    private final CustomerDeliveryInfoService customerDeliveryInfoService;
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

        CustomerDelivery deliveryInfo = getOrCreateDeliveryInfo(userId, dto.customerDeliveryDTO());

        Order order = buildNewOrder(userId, dto, deliveryInfo);

        orderRepository.saveAndFlush(order);

        log.info("Order with id: {} was created", order.getId());

        try {
            ProductReservationInfo reservationInfo = reserveOrderProducts(order);
            order.applyReservation(reservationInfo);
        } catch (Exception e) {
            cancelFailedOrder(order, e, userId);

            log.warn("Failed to reserve products: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product reservation failed");
        }

        orderRepository.save(order);

        orderEventDispatcher.publishCreated(order);

        return order;
    }

    @Transactional
    public void confirmOrder(Long id, OrderConfirmationInfo confirmationInfo) {
        log.info("Confirming order with id: {}", id);

        Order order = getOrderById(id);
        order.confirm();
        orderRepository.save(order);

        orderEventDispatcher.publishConfirmed(order, confirmationInfo);
    }

    @Transactional
    public void shipOrder(Long id, OrderShippingDTO shippingDTO) {
        log.info("Shipping order with id: {}", id);

        Order order = getOrderById(id);
        order.ship();
        orderRepository.save(order);

        CustomerDelivery deliveryInfo = order.getCustomerDeliveryInfo();
        OrderShippingInfo shippingInfo = OrderShippingInfoMapper.toDomain(shippingDTO, deliveryInfo);

        orderEventDispatcher.publishShipped(order, shippingInfo);
    }

    @Transactional
    public void deliverOrder(Long id, OrderDeliveryDTO deliveryDTO) {
        log.info("Setting Order with id: {} as delivered", id);

        Order order = getOrderById(id);
        order.deliver();

        catalogServiceClient.deductReserve(order.getId());
        orderRepository.save(order);

        OrderDeliveryInfo orderDeliveryInfo = OrderDeliveryInfoMapper.toDomain(deliveryDTO, order.getCustomerDeliveryInfo());

        orderEventDispatcher.publishDelivered(order, orderDeliveryInfo);
    }

    @Transactional
    public void cancelOrder(Long id, OrderCancellationInfo cancellationInfo) {
        log.info("Trying to cancel order with id: {}", id);

        Order order = getOrderById(id);
        order.cancel(cancellationInfo);
        orderRepository.save(order);

        catalogServiceClient.revertReserve(order.getId());

        orderEventDispatcher.publishCancelled(order, cancellationInfo);
    }

    @Transactional
    public void setPaymentPaid(KafkaPaymentEvent event) {
        log.info("Changing PaymentStatus to COMPLETED of order with id: {}", event.orderId());

        Order order = getOrderById(event.orderId());
        order.setPaymentId(event.paymentId());
        order.setPaymentStatus(PaymentStatus.valueOf(event.eventType()));
        orderRepository.save(order);

        historyService.saveOrderHistory(event);
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
    public void changePaymentStatus(KafkaPaymentEvent event) {
        log.info("Changing PaymentStatus of order with id: {}", event.orderId());

        Order order = getOrderById(event.orderId());
        order.setPaymentStatus(PaymentStatus.valueOf(event.eventType()));
        orderRepository.save(order);
    }

    @Transactional
    public ProductReservationInfo reserveOrderProducts(Order order) {
        log.info("Reserving OrderProducts from CatalogService");

        Map<Long, Integer> productsMap = order.getProducts().stream()
                .collect(Collectors.toMap(OrderProduct::getProductId, OrderProduct::getQuantity));

        return catalogServiceClient.reserveProducts(ProductReservationDTO.builder()
                .customerUserId(order.getCustomerUserId())
                .orderId(order.getId())
                .productsMap(productsMap)
                .build());
    }

    private CustomerDelivery getOrCreateDeliveryInfo(Long customerUserId, CustomerDeliveryDTO dto) {
        CustomerDelivery deliveryInfo;

        if (dto.isNewDeliveryInfo()) {
            deliveryInfo = customerDeliveryInfoService.create(customerUserId, dto);
        } else {
            deliveryInfo = customerDeliveryInfoService.getByCustomerUserId(customerUserId);
        }

        return deliveryInfo;
    }

    private static Order buildNewOrder(Long userId, OrderCreateRequestDTO dto,
                                       CustomerDelivery deliveryInfo) {
        List<OrderProduct> orderProductList = dto.products().stream()
                .map(OrderProductMapper::fromDto)
                .toList();

        Order order = Order.builder()
                .customerUserId(userId)
                .customerDeliveryInfo(deliveryInfo)
                .orderStatus(OrderStatus.CREATED)
                .provider(Provider.STRIPE)
                .paymentStatus(PaymentStatus.PAYMENT_PENDING)
                .totalPrice(BigDecimal.ZERO)
                .build();

        order.addProducts(orderProductList);

        return order;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    void cancelFailedOrder(Order order, Exception e, Long userId) {
        cancelOrder(order.getId(), OrderCancellationInfo.builder()
                .cancellationMessage("Error during products reservation: %s".formatted(e.getMessage()))
                .cancellationReason("RESERVATION ERROR")
                .sourceUserId(userId)
                .build());
    }
}
