package kz.molten.techshop.orderservice.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic OrderStatusTopic() {
        return TopicBuilder.name("order-status-events")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
