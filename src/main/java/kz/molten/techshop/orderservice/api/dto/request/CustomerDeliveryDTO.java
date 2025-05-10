package kz.molten.techshop.orderservice.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CustomerDeliveryDTO(
        @NotBlank String address
) {
}
