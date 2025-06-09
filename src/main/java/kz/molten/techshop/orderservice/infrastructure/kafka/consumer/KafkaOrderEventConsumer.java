package kz.molten.techshop.orderservice.infrastructure.kafka.consumer;

import kz.molten.techshop.orderservice.application.service.OrderHistoryService;
import kz.molten.techshop.orderservice.application.service.PaymentStatusEventProcessor;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderEventConsumer implements OrderEventConsumer {
    private final OrderHistoryService orderHistoryService;
    private final PaymentStatusEventProcessor paymentStatusEventProcessor;

    @Override
    @Transactional
    public void processOrderEvent(KafkaOrderEvent orderEvent) {
        if (existsByEventId(orderEvent.getEventId())) {
            log.warn("Event with EventId: {} has already been processed", orderEvent.getEventId());
        }
    }

    private static boolean isPaymentStatusEvent(String eventType) {
        return eventType.equals("PAYMENT_COMPLETED")
                || eventType.equals("PAYMENT_FAILED")
                || eventType.equals("PAYMENT_EXPIRED");
    }

    private boolean existsByEventId(UUID eventId) {
        return orderHistoryService.existsByEventId(eventId);
    }
}
