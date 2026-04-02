package com.grootan.storeflow.mapper;

import com.grootan.storeflow.dto.response.OrderItemResponseDto;
import com.grootan.storeflow.dto.response.OrderResponseDto;
import com.grootan.storeflow.models.Order;
import com.grootan.storeflow.models.OrderItem;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final ProductMapper productMapper;
    
    public OrderResponseDto toDto(Order order) {
        if (order == null) return null;
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setReferenceNumber(order.getReferenceNumber());
        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getId());
        }
        if (order.getStatus() != null) {
            dto.setStatus(order.getStatus().name());
        }
        dto.setShippingStreet(order.getShippingStreet());
        dto.setShippingCity(order.getShippingCity());
        dto.setShippingCountry(order.getShippingCountry());
        dto.setShippingPostalCode(order.getShippingPostalCode());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(this::toItemDto).collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    public OrderItemResponseDto toItemDto(OrderItem item) {
        if (item == null) return null;
        OrderItemResponseDto dto = new OrderItemResponseDto();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());
        dto.setProduct(productMapper.toDto(item.getProduct()));
        return dto;
    }
}
