package kz.molten.techshop.orderservice.infrastructure.external;

import jakarta.annotation.Resource;
import kz.molten.techshop.orderservice.domain.model.info.ProductReservationInfo;
import kz.molten.techshop.orderservice.infrastructure.external.dto.ProductReservationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogServiceClientImpl implements CatalogServiceClient {
    private static final String PRODUCT_RESERVATION_URL = "catalog/product-reservation";

    @Resource(name = "catalogServiceWebClient")
    private final WebClient catalogServiceWebClient;

    @Override
    public ProductReservationInfo reserveProducts(ProductReservationDTO reservationDTO) {
        log.info("Reserving products from CatalogService");

        return catalogServiceWebClient.post()
                .uri(PRODUCT_RESERVATION_URL)
                .bodyValue(reservationDTO)
                .retrieve()
                .bodyToMono(ProductReservationInfo.class)
                .log()
                .block();
    }

    @Override
    public void revertReserve(Long orderId) {
        log.info("Reverting products reservations from CatalogService");

        catalogServiceWebClient.post()
                .uri(PRODUCT_RESERVATION_URL + "/" + orderId + "/revert")
                .retrieve()
                .toBodilessEntity()
                .log()
                .block();
    }

    @Override
    public void deductReserve(Long orderId) {
        log.info("Releasing products reservations from CatalogService");

        catalogServiceWebClient.post()
                .uri(PRODUCT_RESERVATION_URL + "/" + orderId + "/deduct")
                .retrieve()
                .toBodilessEntity()
                .log()
                .block();
    }
}
