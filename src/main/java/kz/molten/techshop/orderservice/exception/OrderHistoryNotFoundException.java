package kz.molten.techshop.orderservice.exception;

public class OrderHistoryNotFoundException extends RuntimeException {
    public OrderHistoryNotFoundException(Long id) {
        super("OrderHistory with id: %d was not found".formatted(id));
    }
}
