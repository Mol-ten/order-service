package kz.molten.techshop.orderservice.dto.mapper;

import kz.molten.techshop.orderservice.dto.OrderProductDTO;
import kz.molten.techshop.orderservice.entity.OrderProduct;

public class OrderProductMapper {
    public static OrderProduct fromDto(OrderProductDTO dto) {
        return  OrderProduct.builder()
                .productId(dto.productId())
                .quantity(dto.quantity())
                .build();
    }
}
