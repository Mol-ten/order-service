package kz.molten.techshop.orderservice.enumeration;

public enum OrderStatus {
    ORDER_CREATED,
    ORDER_CONFIRMED,
    PAYMENT_PENDING,
    PAYMENT_FAILED,
    PAYMENT_EXPIRED,
    PAYMENT_COMPLETED,
    DELIVERY,
    DELIVERED,
    CANCELLED
}
