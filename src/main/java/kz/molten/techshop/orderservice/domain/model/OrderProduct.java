package kz.molten.techshop.orderservice.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(schema = "order_service", name = "order_products")
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull
    @Column(name = "product_id")
    private Long productId;

    @NotNull
    @Column(name = "quantity")
    @Min(1)
    private Integer quantity;

    @NotNull
    @Column(name = "fixed_price")
    @Min(0)
    private BigDecimal fixedPrice;

    public BigDecimal calculateTotalPrice() {
        return fixedPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
