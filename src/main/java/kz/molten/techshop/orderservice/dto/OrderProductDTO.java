package kz.molten.techshop.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderProductDTO(
        @NotNull @Min(1) Long productId,
        @NotNull @Min(1) Integer quantity
) {
}
