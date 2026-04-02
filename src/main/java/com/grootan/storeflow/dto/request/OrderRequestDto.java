package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDto {
    @NotEmpty(message = "Order must have items")
    private List<OrderItemRequestDto> items;
    
    @NotNull(message = "Shipping address is required")
    private ShippingAddressDto shippingAddress;
}
