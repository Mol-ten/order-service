package kz.molten.techshop.orderservice.service;

import kz.molten.techshop.orderservice.domain.mapper.OrderEventMapper;
import kz.molten.techshop.orderservice.dto.ExecutedOrderHistoryDTO;
import kz.molten.techshop.orderservice.dto.OrderCreateRequestDTO;
import kz.molten.techshop.orderservice.dto.mapper.OrderProductMapper;
import kz.molten.techshop.orderservice.entity.CatalogProduct;
import kz.molten.techshop.orderservice.entity.Order;
import kz.molten.techshop.orderservice.entity.OrderProduct;
import kz.molten.techshop.orderservice.enumeration.OrderStatus;
import kz.molten.techshop.orderservice.exception.InsufficientOrderProductQuantityException;
import kz.molten.techshop.orderservice.exception.OrderNotFoundException;
import kz.molten.techshop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;
    private final CatalogServiceClient catalogServiceClient;
    private final OrderEventPublisher orderEventPublisher;

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

        List<OrderProduct> orderProducts = dto.products()
                .stream()
                .map(productDto -> {
                    OrderProduct orderProduct = OrderProductMapper.fromDto(productDto);
                    CatalogProduct catalogProduct = catalogServiceClient.getProduct(productDto.productId());
                    validateProductAvailableQuantity(orderProduct, catalogProduct);
                    orderProduct.setFixedPrice(catalogProduct.price());
                    orderProduct.setOrder(order);
                    return orderProduct;
                })
                .toList();

        order.setProducts(orderProducts);
        order.setTotalPrice(calculateTotalPriceOfProducts(orderProducts));

        orderRepository.save(order);

        ExecutedOrderHistoryDTO executedOrderHistoryDTO = ExecutedOrderHistoryDTO.builder()
                .executedAt(Instant.now())
                .performedBy((long) 6)
                .details("")
                .build();

        eventPublisher.publishEvent(OrderEventMapper.toCreatedEvent(order, executedOrderHistoryDTO));
        orderRepository.flush();
        orderEventPublisher.publish();

        return order;
    }

    private void validateProductAvailableQuantity(OrderProduct orderProduct, CatalogProduct catalogProduct) {
        log.info("Validating requested order product quantity with available quantity in catalog service. OrderProductId: {}",
                orderProduct.getId());

        Integer availableQuantity = catalogProduct.quantity();

        if (availableQuantity < orderProduct.getQuantity()) {
            throw new InsufficientOrderProductQuantityException("Catalog can only offer %d pieces of OrderProduct with id: %d"
                    .formatted(availableQuantity, orderProduct.getId())); // TODO: переделать логирование либо передавать DTO, в рамках транзакции не создается оюъект и нет ID
        }
    }

    private static BigDecimal calculateTotalPriceOfProducts(Collection<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .map(OrderProduct::calculateTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
