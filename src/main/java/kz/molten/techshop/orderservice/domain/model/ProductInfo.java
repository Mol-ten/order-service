package kz.molten.techshop.orderservice.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Builder
@Getter
@ToString
public class ProductInfo {
    private final Long id;
    private final BigDecimal price;
    private final Integer quantity;
}
