package kz.molten.techshop.orderservice.domain.repository;

import kz.molten.techshop.orderservice.domain.model.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findAllByOrderIdOrderByCreatedAt(Long orderId);

    boolean existsByEventId(UUID eventId);
}
