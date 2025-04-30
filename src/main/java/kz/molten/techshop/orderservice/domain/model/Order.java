package kz.molten.techshop.orderservice.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "order_service", name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "customer_user_id")
    private Long customerUserId;

    @Min(0)
    @Column(name = "payment_id")
    private Long paymentId;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<@NotNull OrderProduct> products = new ArrayList<>();

    @NotNull
    @Min(0)
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @NotNull
    private OrderStatus orderStatus;

    @NotNull
    private Provider provider;

    @CreationTimestamp(source = SourceType.DB)
    private Instant createdAt;

    @UpdateTimestamp(source = SourceType.DB)
    private Instant updatedAt;

    private String error_message;

    public void addProduct(OrderProduct orderProduct) {
        this.products.add(orderProduct);
        orderProduct.setOrder(this);
    }

    public void calculateTotalPrice() {
        this.totalPrice = products.stream()
                .map(OrderProduct::calculateTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
