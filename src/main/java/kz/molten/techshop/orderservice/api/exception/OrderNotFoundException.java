package kz.molten.techshop.orderservice.api.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("Order with id: %s was not found".formatted(id));
    }
}
