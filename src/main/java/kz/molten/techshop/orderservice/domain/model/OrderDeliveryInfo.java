package kz.molten.techshop.orderservice.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class OrderDeliveryInfo {
    private Long customerUserId;
    private Long courierId;
    private LocalDateTime deliveryDateTime;
}
