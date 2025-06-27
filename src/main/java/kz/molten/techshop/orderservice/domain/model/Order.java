package kz.molten.techshop.orderservice.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kz.molten.techshop.orderservice.api.exception.IllegalOrderStatusException;
import kz.molten.techshop.orderservice.api.exception.IllegalPaymentStatusException;
import kz.molten.techshop.orderservice.api.exception.OrderIllegalAccessException;
import kz.molten.techshop.orderservice.domain.model.enumeration.OrderStatus;
import kz.molten.techshop.orderservice.domain.model.enumeration.PaymentStatus;
import kz.molten.techshop.orderservice.domain.model.enumeration.Provider;
import kz.molten.techshop.orderservice.domain.model.info.OrderCancellationInfo;
import kz.molten.techshop.orderservice.domain.model.info.ProductInfo;
import kz.molten.techshop.orderservice.domain.model.info.ProductReservationInfo;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


@Entity
@Table(schema = "order_service", name = "orders")

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_gen")
    @SequenceGenerator(name = "order_id_gen", sequenceName = "orders_id_seq")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "customer_user_id")
    private Long customerUserId;

    @ManyToOne
    @JoinColumn(name = "customer_delivery_info_id", nullable = false)
    private CustomerDelivery customerDeliveryInfo;

    @Min(0)
    @Column(name = "payment_id")
    private Long paymentId;

    @Builder.Default
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<@NotNull OrderProduct> products = new ArrayList<>();

    @NotNull
    @Min(0)
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderHistory> history;

    @NotNull
    private Provider provider;

    @CreationTimestamp(source = SourceType.DB)
    private Instant createdAt;

    @UpdateTimestamp(source = SourceType.DB)
    private Instant updatedAt;

    private String error_message;

    public void confirm() {
        validateOrderStatusEquals(OrderStatus.CREATED);
        this.orderStatus = OrderStatus.CONFIRMED;
    }

    public void ship() {
        validateOrderPaid();
        validateOrderStatusEquals(OrderStatus.CONFIRMED);
        this.orderStatus = OrderStatus.SHIPPED;
    }

    public void deliver() {
        validateOrderStatusEquals(OrderStatus.SHIPPED);
        this.orderStatus = OrderStatus.DELIVERED;
    }

    public void cancel(OrderCancellationInfo cancellationInfo) {
        if (!Objects.equals(this.getCustomerUserId(), cancellationInfo.getSourceUserId())
                && !Objects.equals(cancellationInfo.getSourceUserId(), 2L)) {
            throw new OrderIllegalAccessException("There is no order with id: %d in your list of orders".formatted(id));
        }

        if (this.paymentStatus.equals(PaymentStatus.PAYMENT_COMPLETED)) {
            throw new IllegalPaymentStatusException("You can't cancel order that has already been paid");
        }

        this.orderStatus = OrderStatus.CANCELLED;
    }

    public void applyReservation(ProductReservationInfo reservationInfo) {
        if (!Objects.equals(reservationInfo.getOrderId(), this.id)) {
            throw new IllegalArgumentException("Reservation info does not match this order");
        }

        Map<Long, ProductInfo> productInfoMap = reservationInfo.getReservedProducts()
                .stream()
                .collect(Collectors.toMap(ProductInfo::getId, Function.identity()));

        finalizePricing(productInfoMap);
    }

    private void finalizePricing(Map<Long, ProductInfo> productInfoMap) {
        for (OrderProduct orderProduct : products) {
            ProductInfo productInfo = productInfoMap.get(orderProduct.getProductId());

            if (productInfo == null) {
                throw new IllegalStateException("Missing reservation for product: " + orderProduct.getProductId());
            }

            orderProduct.applyReservation(productInfo);
        }

        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        this.totalPrice = products.stream()
                .map(OrderProduct::calculateTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addProducts(List<OrderProduct> orderProducts) {
        orderProducts.forEach(orderProduct -> {
            orderProduct.setOrder(this);
            this.products.add(orderProduct);
        });
    }

    public void validateOrderPaid() {
        if (!this.paymentStatus.equals(PaymentStatus.PAYMENT_COMPLETED)) {
            throw new IllegalPaymentStatusException("PaymentStatus \"PAYMENT_COMPLETED\" is required to ship order");
        }
    }

    public void validateOrderStatusEquals(OrderStatus orderStatus) {
        if (!this.orderStatus.equals(orderStatus)) {
            throw new IllegalOrderStatusException("Expected OrderStatus: %s, Provided OrderStatus: %s"
                    .formatted(orderStatus, this.orderStatus));
        }
    }
}
