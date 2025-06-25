package kz.molten.techshop.orderservice.domain.model.info;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class OrderConfirmationInfo {
    private Long managerUserId;
    @Setter
    private Long customerUserId;
    private String confirmationMessage;
    private String confirmationSource;
}
