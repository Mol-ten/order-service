package kz.molten.techshop.orderservice.application.mapper;

import kz.molten.techshop.orderservice.domain.model.OrderHistoryStep;
import kz.molten.techshop.orderservice.domain.model.OrderStatus;
import kz.molten.techshop.orderservice.domain.model.PaymentStatus;

import java.util.Optional;

public class OrderHistoryStepMapper {
    public static Optional<OrderHistoryStep> mapFromOrderStatus(OrderStatus orderStatus) {
        return switch (orderStatus) {
            case CREATED -> Optional.of(OrderHistoryStep.ORDER_CREATED);
            case CONFIRMED -> Optional.of(OrderHistoryStep.ORDER_CONFIRMED);
            case SHIPPED -> Optional.of(OrderHistoryStep.SHIPPED);
            case DELIVERED -> Optional.of(OrderHistoryStep.DELIVERED);
            case CANCELLED -> Optional.of(OrderHistoryStep.CANCELLED);
        };
    }

    public static Optional<OrderHistoryStep> mapFromPaymentStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case PAYMENT_PENDING -> Optional.empty();
            case PAYMENT_COMPLETED -> Optional.of(OrderHistoryStep.PAYMENT_COMPLETED);
            case PAYMENT_FAILED -> Optional.of(OrderHistoryStep.PAYMENT_FAILED);
            case PAYMENT_EXPIRED -> Optional.of(OrderHistoryStep.PAYMENT_EXPIRED);
        };
    }
}
