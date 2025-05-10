package kz.molten.techshop.orderservice.application.mapper;

import kz.molten.techshop.orderservice.api.dto.request.CustomerDeliveryDTO;
import kz.molten.techshop.orderservice.domain.model.CustomerDeliveryInfo;

public class CustomerDeliveryInfoMapper {
    public static CustomerDeliveryInfo toDomain(CustomerDeliveryDTO dto, Long customerUserId) {
        return CustomerDeliveryInfo.builder()
                .customerUserId(customerUserId)
                .address(dto.address())
                .build();
    }
}
