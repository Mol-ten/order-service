package kz.molten.techshop.orderservice.infrastructure.external.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record ProductReservationDTO(
        Long orderId,
        Long customerUserId,
        Map<Long, Integer> productsMap
) {
}
