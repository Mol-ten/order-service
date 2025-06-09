package kz.molten.techshop.orderservice.domain.model.info;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@Getter
public class ProductReservationInfo {
    private Long id;
    private Timestamp creationTimestamp;
    private List<ProductInfo> reservedProducts;
    private Long orderId;
    private Long customerUserId;
    private String productReservationStatus;
}
