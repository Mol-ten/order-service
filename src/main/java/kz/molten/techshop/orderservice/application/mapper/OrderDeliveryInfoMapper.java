package kz.molten.techshop.orderservice.application.mapper;

import kz.molten.techshop.orderservice.api.dto.request.OrderDeliveryDTO;
import kz.molten.techshop.orderservice.domain.model.CustomerDelivery;
import kz.molten.techshop.orderservice.domain.model.info.OrderDeliveryInfo;

import java.time.LocalDateTime;

public class OrderDeliveryInfoMapper {
    public static OrderDeliveryInfo toDomain(OrderDeliveryDTO deliveryDTO, CustomerDelivery customerDeliveryInfo) {
        return OrderDeliveryInfo.builder()
                .customerDeliveryInfo(customerDeliveryInfo)
                .customerUserId(customerDeliveryInfo.getCustomerUserId())
                .courierId(deliveryDTO.courierId())
                .deliveryDateTime(LocalDateTime.now())
                .build();
    }
}
