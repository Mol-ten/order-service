package kz.molten.techshop.orderservice.api.exception;

public class IllegalPaymentStatusException extends RuntimeException {
    public IllegalPaymentStatusException(String message) {
        super(message);
    }
}
