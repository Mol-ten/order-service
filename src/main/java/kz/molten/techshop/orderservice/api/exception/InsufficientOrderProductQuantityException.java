package kz.molten.techshop.orderservice.api.exception;

public class InsufficientOrderProductQuantityException extends RuntimeException {
    public InsufficientOrderProductQuantityException(String message) {
        super(message);
    }
}
