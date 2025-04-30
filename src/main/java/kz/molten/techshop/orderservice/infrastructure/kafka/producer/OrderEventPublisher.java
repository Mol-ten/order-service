package kz.molten.techshop.orderservice.infrastructure.kafka.producer;

import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaOrderEvent;

public interface OrderEventPublisher {
    void publish(KafkaOrderEvent orderEvent);
}
