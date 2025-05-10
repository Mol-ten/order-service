package kz.molten.techshop.orderservice.api.exception;

public class OrderIllegalAccessException extends RuntimeException {
    public OrderIllegalAccessException(String message) {
        super(message);
    }
}
