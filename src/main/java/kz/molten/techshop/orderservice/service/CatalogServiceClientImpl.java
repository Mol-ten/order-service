package kz.molten.techshop.orderservice.service;

import jakarta.annotation.Resource;
import kz.molten.techshop.orderservice.entity.CatalogProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogServiceClientImpl implements CatalogServiceClient {

    @Resource(name = "catalogServiceWebClient")
    private final WebClient  catalogServiceWebClient;
    private final TokenService tokenService;

    @Override
    public CatalogProduct getProduct(Long productId) {
        log.info("Fetching available quantity of product with id: {}", productId);

        return catalogServiceWebClient.get()
                .uri("catalog/product/" + productId)
                .headers(headers -> headers.setBearerAuth(tokenService.getJwt()))
                .retrieve()
                .bodyToMono(CatalogProduct.class)
                .block();
    }
}
