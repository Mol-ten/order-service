package kz.molten.techshop.orderservice.application.mapper;

import kz.molten.techshop.orderservice.api.dto.request.OrderProductDTO;
import kz.molten.techshop.orderservice.domain.model.OrderProduct;

public class OrderProductMapper {
    public static OrderProduct fromDto(OrderProductDTO dto) {
        return  OrderProduct.builder()
                .productId(dto.productId())
                .quantity(dto.quantity())
                .build();
    }
}
