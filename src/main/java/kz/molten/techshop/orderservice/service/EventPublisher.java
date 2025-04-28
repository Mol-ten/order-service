package kz.molten.techshop.orderservice.service;

import kz.molten.techshop.orderservice.dto.OrderEventDTO;

public interface EventPublisher {
    void publish(OrderEventDTO orderEventDTO);
}
