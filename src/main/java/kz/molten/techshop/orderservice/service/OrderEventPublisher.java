package kz.molten.techshop.orderservice.service;

import kz.molten.techshop.orderservice.dto.OrderEventDTO;
import kz.molten.techshop.orderservice.kafka.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventPublisher implements EventPublisher{
    private final KafkaProducer orderEventProducer;

    @Override
    public void publish(OrderEventDTO orderEventDTO) {
        orderEventProducer.sendOrderStatusChange(orderEventDTO);
    }
}
