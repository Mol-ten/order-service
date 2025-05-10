package kz.molten.techshop.orderservice.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class OrderConfirmationInfo {
    private Long managerUserId;
    private String confirmationMessage;
    private String confirmationSource;
}
