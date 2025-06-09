package kz.molten.techshop.orderservice.api.controller;

import jakarta.validation.Valid;
import kz.molten.techshop.orderservice.api.dto.request.*;
import kz.molten.techshop.orderservice.api.exception.UserInsufficientRoleException;
import kz.molten.techshop.orderservice.application.service.OrderService;
import kz.molten.techshop.orderservice.domain.model.Order;
import kz.molten.techshop.orderservice.domain.model.info.OrderCancellationInfo;
import kz.molten.techshop.orderservice.domain.model.info.OrderConfirmationInfo;
import kz.molten.techshop.orderservice.infrastructure.kafka.dto.KafkaOrderEventDTO;
import kz.molten.techshop.orderservice.infrastructure.kafka.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Slf4j
@Validated
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private static final String MANAGER_ROLE = "ROLE_MANAGER";

    private final OrderService orderService;
    private final KafkaProducer kafkaProducer;

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        log.info(order.toString());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Order>> getOrderByUserJwt(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid OrderCreateRequestDTO dto) {
        Long userId = Long.valueOf(jwt.getSubject());

        Order order = orderService.createOrder(userId, dto);

        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/payment-complete")
    public ResponseEntity<String> completePayment(@PathVariable Long orderId) {
        KafkaOrderEventDTO dto = KafkaOrderEventDTO.builder()
                .orderId(orderId)
                .eventType("PAYMENT_COMPLETED")
                .eventId(UUID.randomUUID())
                .timestamp(Timestamp.from(Instant.now()))
                .metadata(Map.of(
                        "details", "PAYMENT COMPLETED!",
                        "paymentStatus", "PAYMENT_COMPLETED"
                ))
                .build();

        kafkaProducer.sendOrderStatusChange(dto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/confirm")
    @ResponseStatus(HttpStatus.OK)
    public void confirmOrder(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                             @RequestBody OrderConfirmationRequestDTO dto) {
        Long userId = Long.valueOf(jwt.getSubject());
        validateJwtRole(jwt, "ROLE_MANAGER");

        OrderConfirmationInfo orderConfirmationInfo = OrderConfirmationInfo.builder()
                .managerUserId(userId)
                .confirmationMessage(dto.message())
                .confirmationSource(dto.confirmationSource())
                .build();

        orderService.confirmOrder(id, orderConfirmationInfo);
    }

    @PostMapping("/{id}/shipped")
    public void shipOrder(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                          @RequestBody @Valid OrderShippingDTO shippingDTO) {
        validateJwtRole(jwt, "ROLE_MANAGER");

        orderService.shipOrder(id, shippingDTO);
    }


    @PostMapping("/{id}/delivered")
    public void deliverOrder(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                             @RequestBody @Valid OrderDeliveryDTO deliveryDTO) {
        validateJwtRole(jwt, "ROLE_MANAGER");
        
        orderService.deliverOrder(id, deliveryDTO);
    }

    @PostMapping("/{id}/cancel")
    public void cancelOrder(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                            @RequestBody OrderCancellationDTO cancellationDTO) {
        Long userId = Long.valueOf(jwt.getSubject());

        OrderCancellationInfo cancellationInfo = OrderCancellationInfo.builder()
                .sourceUserId(userId)
                .cancellationMessage(cancellationDTO.cancellationMessage())
                .cancellationReason(cancellationDTO.cancellationReason())
                .build();

        orderService.cancelOrder(id, cancellationInfo);
    }

    private void validateJwtRole(Jwt jwt, String role) {
        String userClaimsString = jwt.getClaims().get("roles").toString();
        List<String> userClaims = Arrays.stream(userClaimsString.split(", ")).toList();

        if (!userClaims.contains(role)) {
            log.info(userClaims.toString());
            throw new UserInsufficientRoleException("User with id: %s has no %s ROLE".formatted(jwt.getSubject(),
                    MANAGER_ROLE));
        }
    }
}
