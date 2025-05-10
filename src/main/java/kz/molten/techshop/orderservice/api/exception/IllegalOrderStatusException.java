package kz.molten.techshop.orderservice.api.exception;

public class IllegalOrderStatusException extends RuntimeException {
    public IllegalOrderStatusException(String message) {
        super(message);
    }
}
