package kz.molten.techshop.orderservice.kafka.consumer;

import kz.molten.techshop.orderservice.dto.OrderEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final OrderEventConsumer orderEventConsumer;

    @KafkaListener(topics = "order-status-events", groupId = "order-group")
    public void listen(OrderEventDTO event) {
        log.info("New kafka message from \"order-status-events\" topic.");
        orderEventConsumer.processOrderEvent(event);
    }
}
