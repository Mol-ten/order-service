package kz.molten.techshop.orderservice.api.dto.request;

import jakarta.validation.constraints.Min;

public record OrderDeliveryDTO(
        @Min(1) Long customerUserId,
        @Min(1) Long courierId
        ) {
}
