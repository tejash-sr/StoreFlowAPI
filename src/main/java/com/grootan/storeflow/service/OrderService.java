package com.grootan.storeflow.service;

import com.grootan.storeflow.dto.request.OrderRequestDto;
import com.grootan.storeflow.dto.request.OrderStatusUpdateRequestDto;
import com.grootan.storeflow.dto.response.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface OrderService {
    OrderResponseDto placeOrder(UUID customerId, OrderRequestDto requestDto);
    Page<OrderResponseDto> getOrdersForUser(UUID customerId, Pageable pageable);
    OrderResponseDto getOrderById(UUID id);
    OrderResponseDto updateOrderStatus(UUID id, OrderStatusUpdateRequestDto requestDto);
}
