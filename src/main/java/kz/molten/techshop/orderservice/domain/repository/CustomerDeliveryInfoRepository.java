package kz.molten.techshop.orderservice.domain.repository;

import kz.molten.techshop.orderservice.domain.model.CustomerDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerDeliveryInfoRepository extends JpaRepository<CustomerDelivery, Long> {
    Optional<CustomerDelivery> findByCustomerUserId(Long customerUserId);
}
