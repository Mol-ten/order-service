package kz.molten.techshop.orderservice.domain.model;

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
    private String address;
    private LocalDateTime shippingTime;
}
