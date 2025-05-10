package kz.molten.techshop.orderservice.application.service;

import kz.molten.techshop.orderservice.application.mapper.OrderEventMapper;
import kz.molten.techshop.orderservice.domain.event.OrderStatusChangedEvent;
import kz.molten.techshop.orderservice.domain.model.*;
import kz.molten.techshop.orderservice.infrastructure.kafka.event.KafkaOrderEvent;
import kz.molten.techshop.orderservice.infrastructure.kafka.mapper.KafkaOrderEventMapper;
import kz.molten.techshop.orderservice.infrastructure.kafka.producer.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderEventDispatcher {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final OrderEventPublisher kafkaOrderEventPublisher;

    public void publishCreated(Order order) {
        UUID eventId = UUID.randomUUID();

        OrderStatusChangedEvent domainEvent = OrderEventMapper.toCreatedEvent(order, eventId);
        KafkaOrderEvent kafkaEvent = KafkaOrderEventMapper.toCreatedEvent(order, eventId);

        applicationEventPublisher.publishEvent(domainEvent);
        kafkaOrderEventPublisher.publish(kafkaEvent);
    }

    public void publishConfirmed(Order order, OrderConfirmationInfo confirmationInfo) {
        UUID eventId = UUID.randomUUID();

        OrderStatusChangedEvent domainEvent = OrderEventMapper.toConfirmedEvent(order, eventId, confirmationInfo);
        KafkaOrderEvent kafkaEvent = KafkaOrderEventMapper.toConfirmedEvent(order, eventId, confirmationInfo);

        applicationEventPublisher.publishEvent(domainEvent);
        kafkaOrderEventPublisher.publish(kafkaEvent);
    }

    public void publishShipped(Order order, OrderShippingInfo shippingInfo) {
        UUID eventId = UUID.randomUUID();

        OrderStatusChangedEvent domainEvent = OrderEventMapper.toShippedEvent(order, eventId, shippingInfo);
        KafkaOrderEvent kafkaEvent = KafkaOrderEventMapper.toShippedEvent(order, eventId, shippingInfo);

        applicationEventPublisher.publishEvent(domainEvent);
        kafkaOrderEventPublisher.publish(kafkaEvent);
    }

    public void publishDelivered(Order order, OrderDeliveryInfo deliveryInfo) {
        UUID eventId = UUID.randomUUID();

        OrderStatusChangedEvent domainEvent = OrderEventMapper.toDeliveredEvent(order, eventId, deliveryInfo);
        KafkaOrderEvent kafkaEvent = KafkaOrderEventMapper.toDeliveredEvent(order, eventId, deliveryInfo);

        applicationEventPublisher.publishEvent(domainEvent);
        kafkaOrderEventPublisher.publish(kafkaEvent);
    }

    public void publishCancelled(Order order, OrderCancellationInfo cancellationInfo) {
        UUID eventId = UUID.randomUUID();

        OrderStatusChangedEvent domainEvent = OrderEventMapper.toCancelledEvent(order, eventId, cancellationInfo);
        KafkaOrderEvent kafkaEvent = KafkaOrderEventMapper.toCancelledEvent(order, eventId, cancellationInfo);

        applicationEventPublisher.publishEvent(domainEvent);
        kafkaOrderEventPublisher.publish(kafkaEvent);
    }
}
