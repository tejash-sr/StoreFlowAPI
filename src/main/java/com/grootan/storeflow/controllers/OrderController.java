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
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        com.grootan.storeflow.security.CustomUserDetails userDetails = (com.grootan.storeflow.security.CustomUserDetails) auth.getPrincipal();
        UUID customerId = userDetails.getUser().getId(); 
        
        return orderService.placeOrder(customerId, requestDto);
    }

    @GetMapping
    public Page<OrderResponseDto> getOrders(Pageable pageable) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        com.grootan.storeflow.security.CustomUserDetails userDetails = (com.grootan.storeflow.security.CustomUserDetails) auth.getPrincipal();
        UUID customerId = userDetails.getUser().getId();
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
