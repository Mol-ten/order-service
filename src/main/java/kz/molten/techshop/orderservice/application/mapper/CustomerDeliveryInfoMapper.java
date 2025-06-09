package kz.molten.techshop.orderservice.application.mapper;

import kz.molten.techshop.orderservice.api.dto.request.CustomerDeliveryDTO;
import kz.molten.techshop.orderservice.domain.model.CustomerDelivery;

public class CustomerDeliveryInfoMapper {
    public static CustomerDelivery toDomain(CustomerDeliveryDTO dto, Long customerUserId) {
        return CustomerDelivery.builder()
                .customerUserId(customerUserId)
                .address(dto.address())
                .build();
    }
}
