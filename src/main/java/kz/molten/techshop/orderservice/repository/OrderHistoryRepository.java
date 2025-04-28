package kz.molten.techshop.orderservice.repository;

import kz.molten.techshop.orderservice.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findAllByOrderIdOrderByCreatedAt(Long orderId);
}
