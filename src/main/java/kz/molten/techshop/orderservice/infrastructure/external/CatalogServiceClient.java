package kz.molten.techshop.orderservice.infrastructure.external;

import kz.molten.techshop.orderservice.domain.model.ProductInfo;

import java.util.List;
import java.util.Map;

public interface CatalogServiceClient {
    List<ProductInfo> reserveProducts(Map<Long, Integer> productsMap);
    List<ProductInfo> getProducts(List<Long> productIds);
    void revertReserve(Map<Long, Integer> productsMap);
    void releaseReserve(Map<Long, Integer> productsMap);
}
