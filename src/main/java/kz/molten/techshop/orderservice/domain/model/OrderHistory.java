package kz.molten.techshop.orderservice.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kz.molten.techshop.orderservice.domain.model.enumeration.OrderHistoryStep;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "order_service", name = "orders_history")
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_history_id_gen")
    @SequenceGenerator(name = "order_history_id_gen", sequenceName = "orders_history_id_seq")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull
    @Column(name = "order_history_step", nullable = false)
    private OrderHistoryStep orderHistoryStep;

    @Column(name = "event_id")
    private UUID eventId;

    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "executed_at")
    private Instant executedAt;

    @Column(name = "details")
    private String details;

    @Column(name = "performed_by")
    private Long performedBy;
}
