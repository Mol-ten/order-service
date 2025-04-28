package kz.molten.techshop.orderservice.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;

public record CatalogProduct(
        @NotNull @Min(0) Long id,
        @NotBlank String name,
        @NotNull @Min(0) BigDecimal price,
        @NotNull @Min(0) Integer quantity,
        @NotBlank String description,
        @NotBlank String productType,
        Map<String, Object> productAttributes,
        boolean isOutOfStock
        ) {
}
