package kz.molten.techshop.orderservice.exception;

public class OrderAccessViolationException extends RuntimeException {
    public OrderAccessViolationException(String message) {
        super(message);
    }
}
