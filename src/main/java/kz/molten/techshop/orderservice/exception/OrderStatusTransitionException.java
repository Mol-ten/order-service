package kz.molten.techshop.orderservice.exception;

public class OrderStatusTransitionException extends RuntimeException {
    public OrderStatusTransitionException(String message) {
        super(message);
    }
}
