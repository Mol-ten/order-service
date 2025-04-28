package kz.molten.techshop.orderservice.domain.event;

import kz.molten.techshop.orderservice.dto.ExecutedOrderHistoryDTO;
import kz.molten.techshop.orderservice.entity.OrderProduct;
import kz.molten.techshop.orderservice.enumeration.Provider;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
@Getter
@ToString
public class OrderCreatedEvent {
    private final Long orderId;
    private final Long customerUserId;
    private final List<OrderProduct> products;
    private final BigDecimal totalPrice;
    private final Provider provider;
    private final Instant createdAt;
    private final Instant executedAt;
    private final String details;
    private final Long performedBy;
}
