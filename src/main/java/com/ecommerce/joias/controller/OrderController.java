package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.create.CreateOrderDto;
import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.response.OrderResponseDto;
import com.ecommerce.joias.dto.update.UpdateOrderDto;
import com.ecommerce.joias.entity.enums.OrderStatus;
import com.ecommerce.joias.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/user/{userId}/address/{addressId}")
    public ResponseEntity<OrderResponseDto> createOrder(@PathVariable("userId") UUID userId, @PathVariable("addressId") Integer addressId, @RequestBody CreateOrderDto createOrderDto) {
        var orderDto = orderService.createOrder(userId, addressId, createOrderDto);

        URI location = URI.create("/orders/" + orderDto.orderId());

        return ResponseEntity.created(location).body(orderDto);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable("orderId") Integer orderId) {
        var orderDto = orderService.getOrderById(orderId);

        return ResponseEntity.ok(orderDto);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<OrderResponseDto>> listOrders(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit,
            @RequestParam(name = "status", required = false)OrderStatus orderStatus
            ) {
        var ordersDto = orderService.listOrders(page, limit, orderStatus);

        return ResponseEntity.ok(ordersDto);
    }

    @GetMapping("/revenue/total")
    public ResponseEntity<Double> getTotalFaturado() {
        Double total = orderService.getTotalFaturado();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/stats/products-sold-month")
    public ResponseEntity<Integer> getProductsSoldMonth() {
        return ResponseEntity.ok(orderService.getProdutosVendidosNoMes());
    }

    @GetMapping("/pending-delivery")
    public ResponseEntity<Integer> getPendingDelivery() {
        var countPendingDelivery = orderService.getCountPendingDelivery();

        return ResponseEntity.ok(countPendingDelivery);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<OrderResponseDto>> getOrdersByUserId(
            @PathVariable("userId") UUID userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {

        var response = orderService.findAllByUserId(userId, page, limit);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Void> updateOrderById(@PathVariable("orderId") Integer orderId, @RequestBody UpdateOrderDto updateOrderDto) {
        orderService.updateOrderById(orderId, updateOrderDto);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{orderId}/tracking")
    public ResponseEntity<Void> updateTrackingCode(
            @PathVariable("orderId") Integer orderId,
            @RequestBody Map<String, String> payload
    ) {
        String trackingCode = payload.get("trackingCode");
        orderService.updateTrackingAndShip(orderId, trackingCode);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable("orderId") Integer orderId) {
        orderService.deleteOrderById(orderId);

        return ResponseEntity.noContent().build();
    }
}
