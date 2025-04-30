package kz.molten.techshop.orderservice.api.exception;

public class OrderAccessViolationException extends RuntimeException {
    public OrderAccessViolationException(String message) {
        super(message);
    }
}
