package kz.molten.techshop.orderservice.infrastructure.external.dto;

import java.math.BigDecimal;

public record ReservedProductDTO(
        Long productId,
        Integer reservedQuantity,
        BigDecimal reservedPrice
) {
}
