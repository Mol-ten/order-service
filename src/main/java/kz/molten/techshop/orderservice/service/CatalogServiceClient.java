package kz.molten.techshop.orderservice.service;

import kz.molten.techshop.orderservice.entity.CatalogProduct;

public interface CatalogServiceClient {
    CatalogProduct getProduct(Long productId);
}
