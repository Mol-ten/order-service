package kz.molten.techshop.orderservice.infrastructure.kafka.consumer;

import kz.molten.techshop.orderservice.infrastructure.kafka.dto.KafkaOrderEventDTO;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaOrderEvent;
import kz.molten.techshop.orderservice.infrastructure.kafka.mapper.KafkaOrderEventMapper;
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
    public void listen(KafkaOrderEventDTO eventDTO) {
        log.info("New kafka message from \"order-status-events\" topic.");

        KafkaOrderEvent orderEvent = KafkaOrderEventMapper.toDomain(eventDTO);
        orderEventConsumer.processOrderEvent(orderEvent);
    }


}
