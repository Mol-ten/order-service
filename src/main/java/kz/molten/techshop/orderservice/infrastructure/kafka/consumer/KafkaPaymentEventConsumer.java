package kz.molten.techshop.orderservice.infrastructure.kafka.consumer;

import kz.molten.techshop.orderservice.application.service.PaymentStatusEventProcessor;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaPaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPaymentEventConsumer implements PaymentEventConsumer {
    private final PaymentStatusEventProcessor paymentStatusEventProcessor;

    @Override
    public void consume(KafkaPaymentEvent paymentEvent) {
        paymentStatusEventProcessor.process(paymentEvent);
    }
}
