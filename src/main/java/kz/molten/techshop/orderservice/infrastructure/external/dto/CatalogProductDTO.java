package kz.molten.techshop.orderservice.infrastructure.external.dto;

import java.math.BigDecimal;
import java.util.Map;

public record CatalogProductDTO(
        Long id,
        String name,
        BigDecimal price,
        Integer quantity,
        String description,
        String productType,
        Map<String, Object> productAttributes,
        Boolean isOutOfStock
) {
}
