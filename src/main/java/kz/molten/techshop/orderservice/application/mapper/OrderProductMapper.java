package kz.molten.techshop.orderservice.application.mapper;

import kz.molten.techshop.orderservice.api.dto.request.OrderProductDTO;
import kz.molten.techshop.orderservice.domain.model.OrderProduct;

import java.math.BigDecimal;

public class OrderProductMapper {
    public static OrderProduct fromDto(OrderProductDTO dto) {
        return  OrderProduct.builder()
                .productId(dto.productId())
                .quantity(dto.quantity())
                .fixedPrice(BigDecimal.ZERO)
                .build();
    }
}
