package kz.molten.techshop.orderservice.exception;

public class InsufficientOrderProductQuantityException extends RuntimeException {
    public InsufficientOrderProductQuantityException(String message) {
        super(message);
    }
}
