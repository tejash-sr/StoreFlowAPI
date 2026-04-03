package com.grootan.storeflow.controllers;

import com.grootan.storeflow.dto.request.OrderRequestDto;
import com.grootan.storeflow.dto.request.OrderStatusUpdateRequestDto;
import com.grootan.storeflow.dto.response.OrderResponseDto;
import com.grootan.storeflow.repository.UserRepository;
import com.grootan.storeflow.security.CustomUserDetails;
import com.grootan.storeflow.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    private UUID getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser().getId();
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new com.grootan.storeflow.exceptions.AppException("User not found", HttpStatus.UNAUTHORIZED))
                .getId();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto placeOrder(@Valid @RequestBody OrderRequestDto requestDto) {
        return orderService.placeOrder(getAuthenticatedUserId(), requestDto);
    }

    @GetMapping
    public Page<OrderResponseDto> getOrders(Pageable pageable) {
        return orderService.getOrdersForUser(getAuthenticatedUserId(), pageable);
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
