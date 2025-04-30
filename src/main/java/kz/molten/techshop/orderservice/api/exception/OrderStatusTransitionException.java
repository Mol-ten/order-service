package kz.molten.techshop.orderservice.api.exception;

public class OrderStatusTransitionException extends RuntimeException {
    public OrderStatusTransitionException(String message) {
        super(message);
    }
}
