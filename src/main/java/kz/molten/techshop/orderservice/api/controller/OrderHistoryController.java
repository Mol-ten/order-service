package kz.molten.techshop.orderservice.api.controller;

import jakarta.validation.Valid;
import kz.molten.techshop.orderservice.api.dto.request.OrderHistoryDTO;
import kz.molten.techshop.orderservice.domain.model.OrderHistory;
import kz.molten.techshop.orderservice.application.service.OrderHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order/history")
@RequiredArgsConstructor
@Validated
public class OrderHistoryController {
    private final OrderHistoryService orderHistoryService;

    @GetMapping("/{orderId}")
    public ResponseEntity<List<OrderHistory>> getOrderHistoryByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderHistoryService.getOrderHistoryByOrder(orderId));
    }

    @PostMapping
    public ResponseEntity<OrderHistory> saveOrderHistory(@RequestBody @Valid OrderHistoryDTO orderHistoryDTO) {
        return ResponseEntity.ok(orderHistoryService.saveOrderHistory(orderHistoryDTO));
    }
}
