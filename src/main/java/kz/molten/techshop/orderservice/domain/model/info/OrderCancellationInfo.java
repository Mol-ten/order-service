package kz.molten.techshop.orderservice.domain.model.info;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class OrderCancellationInfo {
    private Long sourceUserId;
    private String cancellationReason;
    private String cancellationMessage;
}
