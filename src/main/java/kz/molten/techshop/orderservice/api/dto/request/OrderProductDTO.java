package kz.molten.techshop.orderservice.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderProductDTO(
        @NotNull @Min(1) Long productId,
        @NotNull @Min(1) Integer quantity
) {
}
