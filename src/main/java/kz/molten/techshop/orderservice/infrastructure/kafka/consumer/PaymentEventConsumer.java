package kz.molten.techshop.orderservice.infrastructure.kafka.consumer;

import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaPaymentEvent;

public interface PaymentEventConsumer {
    void consume(KafkaPaymentEvent paymentEvent);
}
