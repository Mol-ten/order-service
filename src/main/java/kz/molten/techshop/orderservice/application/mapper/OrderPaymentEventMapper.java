package kz.molten.techshop.orderservice.application.mapper;

import kz.molten.techshop.orderservice.domain.event.PaymentStatusChangedEvent;
import kz.molten.techshop.orderservice.domain.model.enumeration.PaymentStatus;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaOrderEvent;

import java.util.Map;

public class OrderPaymentEventMapper {
    public static PaymentStatusChangedEvent toDomain(KafkaOrderEvent kafkaOrderEvent, PaymentStatus paymentStatus) {
        return PaymentStatusChangedEvent.builder()
                .orderId(kafkaOrderEvent.getOrderId())
                .paymentStatus(paymentStatus)
                .eventId(kafkaOrderEvent.getEventId())
                .timestamp(kafkaOrderEvent.getEventTimestamp())
                .details(extractPaymentStatusDetails(kafkaOrderEvent.getMetadata()))
                .build();
    }

    private static String extractPaymentStatusDetails(Map<String, Object> metadata) {
        Object rawDetails = metadata.get("details");

        if (rawDetails instanceof String details) {
            return details;
        } else {
            throw new IllegalArgumentException("Details are missing or not a string type");
        }
    }
}
