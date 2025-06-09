package kz.molten.techshop.orderservice.infrastructure.external.mapper;

import kz.molten.techshop.orderservice.domain.model.info.ProductInfo;
import kz.molten.techshop.orderservice.infrastructure.external.dto.CatalogProductDTO;
import kz.molten.techshop.orderservice.infrastructure.external.dto.ReservedProductDTO;

public class ProductInfoMapper {

    public static ProductInfo toDomain(CatalogProductDTO dto) {
        return ProductInfo.builder()
                .id(dto.id())
                .price(dto.price())
                .quantity(dto.quantity())
                .build();
    }

    public static ProductInfo toDomain(ReservedProductDTO dto) {
        return ProductInfo.builder()
                .id(dto.productId())
                .quantity(dto.reservedQuantity())
                .price(dto.reservedPrice())
                .build();
    }
}
