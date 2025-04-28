package kz.molten.techshop.orderservice.kafka.consumer;

import kz.molten.techshop.orderservice.dto.OrderEventDTO;
import kz.molten.techshop.orderservice.dto.mapper.OrderEventMapper;
import kz.molten.techshop.orderservice.entity.OrderStatusChangeEvent;
import kz.molten.techshop.orderservice.service.OrderStatusChanger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumerImpl implements OrderEventConsumer {
    private final OrderStatusChanger orderStatusChanger;
    @Override
    public void processOrderEvent(OrderEventDTO eventDTO) {
        OrderStatusChangeEvent event = OrderEventMapper.toDomain(eventDTO);
        orderStatusChanger.changeStatus(event);
    }
}
