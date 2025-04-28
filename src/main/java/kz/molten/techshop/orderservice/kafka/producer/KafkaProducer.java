package kz.molten.techshop.orderservice.kafka.producer;

import kz.molten.techshop.orderservice.dto.OrderEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderStatusChange(OrderEventDTO orderEventDTO) {
        log.info("Sending message to kafka topic \"order-status-events\". OrderId: {}, EventType: {}",
                orderEventDTO.orderId(), orderEventDTO.eventType());
        String orderId = String.valueOf(orderEventDTO.orderId());

        kafkaTemplate.send("order-status-events", orderId, orderEventDTO);
    }
}
