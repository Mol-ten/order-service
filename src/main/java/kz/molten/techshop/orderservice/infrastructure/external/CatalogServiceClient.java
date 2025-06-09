package kz.molten.techshop.orderservice.infrastructure.external;

import kz.molten.techshop.orderservice.domain.model.info.ProductReservationInfo;
import kz.molten.techshop.orderservice.infrastructure.external.dto.ProductReservationDTO;

public interface CatalogServiceClient {
    ProductReservationInfo reserveProducts(ProductReservationDTO reservationDTO);
    void revertReserve(Long orderId);
    void deductReserve(Long orderId);
}
