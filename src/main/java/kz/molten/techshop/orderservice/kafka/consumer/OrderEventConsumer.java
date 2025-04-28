package kz.molten.techshop.orderservice.kafka.consumer;

import kz.molten.techshop.orderservice.dto.OrderEventDTO;

public interface OrderEventConsumer {
    void processOrderEvent(OrderEventDTO eventDTO);
}
