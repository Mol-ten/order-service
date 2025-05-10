package kz.molten.techshop.orderservice.application.service;

import kz.molten.techshop.orderservice.api.exception.IllegalPaymentStatusException;
import kz.molten.techshop.orderservice.application.mapper.OrderPaymentEventMapper;
import kz.molten.techshop.orderservice.domain.event.PaymentStatusChangedEvent;
import kz.molten.techshop.orderservice.domain.model.PaymentStatus;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentStatusEventProcessor {
    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;

    public void process(KafkaOrderEvent event) {
        PaymentStatus paymentStatus = extractPaymentStatus(event.getMetadata());
        PaymentStatusChangedEvent paymentStatusChangedEvent = OrderPaymentEventMapper
                .toDomain(event, paymentStatus);

        eventPublisher.publishEvent(paymentStatusChangedEvent);

        switch (paymentStatus) {
            case PAYMENT_PENDING -> {}
            case PAYMENT_COMPLETED -> handleCompleted(paymentStatusChangedEvent);
            case PAYMENT_FAILED -> handleFailed(paymentStatusChangedEvent);
            case PAYMENT_EXPIRED -> handleExpired(paymentStatusChangedEvent);
        }
    }

    private void handleCompleted(PaymentStatusChangedEvent event) {
    }

    private void handleFailed(PaymentStatusChangedEvent event) {
    }

    private void handleExpired(PaymentStatusChangedEvent event) {
    }

    private PaymentStatus extractPaymentStatus(Map<String, Object> metadata) {
        Object rawStatus = metadata.get("paymentStatus");

        if (rawStatus instanceof String statusStr) {
            try {
                return PaymentStatus.valueOf(statusStr);
            } catch (IllegalArgumentException exception) {
                throw new IllegalPaymentStatusException("Unknown paymentStatus value: %s".formatted(statusStr));
            }
        } else {
            throw new IllegalPaymentStatusException("paymentStatus is missing or not a string type");
        }
    }
}
