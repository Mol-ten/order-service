package kz.molten.techshop.orderservice.infrastructure.kafka.consumer;

import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaOrderEvent;

public interface OrderEventConsumer {
    void processOrderEvent(KafkaOrderEvent orderEvent);
}
