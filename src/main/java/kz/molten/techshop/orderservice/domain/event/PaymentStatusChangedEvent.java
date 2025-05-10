package kz.molten.techshop.orderservice.domain.event;

import kz.molten.techshop.orderservice.domain.model.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.UUID;

@Builder
@Getter
@ToString
public class PaymentStatusChangedEvent {
    private final Long orderId;
    private final UUID eventId;
    private final PaymentStatus paymentStatus;
    private final Timestamp timestamp;
    private final String details;
}
