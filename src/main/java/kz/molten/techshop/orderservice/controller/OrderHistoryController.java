package kz.molten.techshop.orderservice.controller;

import jakarta.validation.Valid;
import kz.molten.techshop.orderservice.dto.OrderHistoryDTO;
import kz.molten.techshop.orderservice.entity.OrderHistory;
import kz.molten.techshop.orderservice.service.OrderHistoryService;
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
