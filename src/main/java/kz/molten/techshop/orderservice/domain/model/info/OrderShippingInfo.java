package kz.molten.techshop.orderservice.domain.model.info;

import kz.molten.techshop.orderservice.domain.model.CustomerDelivery;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class OrderShippingInfo {
    private Long courierId;
    private String message;
    private CustomerDelivery customerDeliveryInfo;
    private LocalDateTime shippingTime;
}
