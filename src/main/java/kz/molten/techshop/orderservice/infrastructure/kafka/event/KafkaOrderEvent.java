package kz.molten.techshop.orderservice.infrastructure.kafka.event;

import kz.molten.techshop.orderservice.domain.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Builder
@Getter
@ToString
public class KafkaOrderEvent {
    private final Long orderId;
    private final OrderStatus orderStatus;
    private final UUID eventId;
    private final Timestamp eventTimestamp;
    private final Map<String, Object> metadata;
}
