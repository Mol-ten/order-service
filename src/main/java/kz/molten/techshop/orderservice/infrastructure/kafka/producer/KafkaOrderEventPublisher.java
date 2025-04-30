package kz.molten.techshop.orderservice.infrastructure.kafka.producer;

import kz.molten.techshop.orderservice.infrastructure.kafka.dto.KafkaOrderEventDTO;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaOrderEvent;
import kz.molten.techshop.orderservice.infrastructure.kafka.mapper.KafkaOrderEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaOrderEventPublisher implements OrderEventPublisher {
    private final KafkaProducer orderEventProducer;

    @Override
    public void publish(KafkaOrderEvent orderEvent) {
        KafkaOrderEventDTO orderEventDTO = KafkaOrderEventMapper.toDto(orderEvent);

        orderEventProducer.sendOrderStatusChange(orderEventDTO);
    }
}
