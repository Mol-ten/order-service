package kz.molten.techshop.orderservice.domain.repository;

import kz.molten.techshop.orderservice.domain.model.Order;
import kz.molten.techshop.orderservice.domain.model.OrderStatus;
import kz.molten.techshop.orderservice.domain.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByCustomerUserId(Long userId);
    List<Order> findAllByCreatedAtBeforeAndPaymentStatusNotAndOrderStatusNot(Instant instant, PaymentStatus paymentStatus,
                                                                             OrderStatus orderStatus);
}
