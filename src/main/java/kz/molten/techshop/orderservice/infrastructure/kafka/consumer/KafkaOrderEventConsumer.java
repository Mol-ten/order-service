package kz.molten.techshop.orderservice.infrastructure.kafka.consumer;

import kz.molten.techshop.orderservice.application.service.OrderHistoryService;
import kz.molten.techshop.orderservice.application.service.OrderStatusChanger;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderEventConsumer implements OrderEventConsumer {
    private final OrderStatusChanger orderStatusChanger;
    private final OrderHistoryService orderHistoryService;

    @Override
    public void processOrderEvent(KafkaOrderEvent orderEvent) {
        if (existsByEventId(orderEvent.getEventId())) {
            log.warn("Event with EventId: {} has already been processed", orderEvent.getEventId());
            return;
        }

        //orderStatusChanger.changeStatus(orderEvent);
    }


    private boolean existsByEventId(UUID eventId) {
        return orderHistoryService.existsByEventId(eventId);
    }
}
