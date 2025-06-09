package kz.molten.techshop.orderservice.application.service;

import kz.molten.techshop.orderservice.api.exception.IllegalPaymentStatusException;
import kz.molten.techshop.orderservice.domain.model.enumeration.PaymentStatus;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaPaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentStatusEventProcessor {
    private final OrderService orderService;


    public void process(KafkaPaymentEvent event) {
        String paymentStatus = event.eventType();

        switch (paymentStatus) {
            case "PAYMENT_PENDING" -> {}
            case "PAYMENT_COMPLETED" -> handleCompleted(event);
            case "PAYMENT_FAILED" -> handleFailed(event);
            case "PAYMENT_EXPIRED" -> handleExpired(event);
        }
    }

    private void handleCompleted(KafkaPaymentEvent event) {
        log.info("Handling PaymentCompleted event");

        orderService.setPaymentPaid(event);
    }

    private void handleFailed(KafkaPaymentEvent event) {
    }

    private void handleExpired(KafkaPaymentEvent event) {
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
