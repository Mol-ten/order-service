package kz.molten.techshop.orderservice.api.exception;

public class CustomerDeliveryInfoNotFoundException extends RuntimeException {
    public CustomerDeliveryInfoNotFoundException(String message) {
        super(message);
    }
}
