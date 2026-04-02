package com.grootan.storeflow.controllers;

import com.grootan.storeflow.dto.request.OrderRequestDto;
import com.grootan.storeflow.dto.request.OrderStatusUpdateRequestDto;
import com.grootan.storeflow.dto.response.OrderResponseDto;
import com.grootan.storeflow.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto placeOrder(@Valid @RequestBody OrderRequestDto requestDto) {
        // Hardcoded admin customer until auth is added in Phase 4
        // Assume customer setup happens in tests/db scripts
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000001"); 
        
        // Let user setup dynamically in test context. For actual endpoint this will be mocked safely in test
        return orderService.placeOrder(customerId, requestDto);
    }

    @GetMapping
    public Page<OrderResponseDto> getOrders(Pageable pageable) {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        return orderService.getOrdersForUser(customerId, pageable);
    }

    @GetMapping("/{id}")
    public OrderResponseDto getOrderById(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @PatchMapping("/{id}/status")
    public OrderResponseDto updateOrderStatus(@PathVariable UUID id, @Valid @RequestBody OrderStatusUpdateRequestDto requestDto) {
        return orderService.updateOrderStatus(id, requestDto);
    }
}
