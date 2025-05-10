package kz.molten.techshop.orderservice.infrastructure.external;

import jakarta.annotation.Resource;
import kz.molten.techshop.orderservice.domain.model.ProductInfo;
import kz.molten.techshop.orderservice.infrastructure.external.dto.CatalogProductDTO;
import kz.molten.techshop.orderservice.infrastructure.external.dto.ReservedProductDTO;
import kz.molten.techshop.orderservice.infrastructure.external.mapper.ProductInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogServiceClientImpl implements CatalogServiceClient {

    @Resource(name = "catalogServiceWebClient")
    private final WebClient catalogServiceWebClient;

    @Override
    public List<ProductInfo> reserveProducts(Map<Long, Integer> productsMap) {
        log.info("Reserving products from CatalogService");

        return catalogServiceWebClient.post()
                .uri("catalog/product/reserve")
                .bodyValue(productsMap)
                .retrieve()
                .bodyToFlux(ReservedProductDTO.class)
                .map(ProductInfoMapper::toDomain)
                .log()
                .collectList()
                .block();
    }

    @Override
    public void revertReserve(Map<Long, Integer> productsMap) {
        log.info("Reverting products reservations from CatalogService");

        catalogServiceWebClient.post()
                .uri("catalog/product/revert-reserve")
                .bodyValue(productsMap)
                .retrieve()
                .toBodilessEntity()
                .log()
                .block();
    }

    @Override
    public void releaseReserve(Map<Long, Integer> productsMap) {
        log.info("Releasing products reservations from CatalogService");

        catalogServiceWebClient.post()
                .uri("catalog/product/release-reserve")
                .bodyValue(productsMap)
                .retrieve()
                .toBodilessEntity()
                .log()
                .block();
    }

    @Override
    public List<ProductInfo> getProducts(List<Long> productIds) {
        log.info("Fetching products with ids: {} from CatalogService", productIds);

        return catalogServiceWebClient.post()
                .uri("catalog/product/list")
                .bodyValue(productIds)
                .retrieve()
                .bodyToFlux(CatalogProductDTO.class)
                .map(ProductInfoMapper::toDomain)
                .collectList()
                .block();
    }

}
