package kz.molten.techshop.orderservice.domain.model.enumeration;

public enum OrderHistoryStep {
    ORDER_CREATED,
    ORDER_CONFIRMED,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,
    PAYMENT_EXPIRED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
