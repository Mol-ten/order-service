package kz.molten.techshop.orderservice.application.mapper;

import kz.molten.techshop.orderservice.api.dto.request.OrderShippingDTO;
import kz.molten.techshop.orderservice.domain.model.CustomerDelivery;
import kz.molten.techshop.orderservice.domain.model.info.OrderShippingInfo;

public class OrderShippingInfoMapper {
    public static OrderShippingInfo toDomain(OrderShippingDTO dto, CustomerDelivery deliveryInfo) {
        return OrderShippingInfo.builder()
                .customerUserId(deliveryInfo.getCustomerUserId())
                .customerDeliveryInfo(deliveryInfo)
                .shippingTime(dto.requestedShippingTime())
                .courierId(dto.courierId())
                .message(dto.message())
                .build();
    }
}
