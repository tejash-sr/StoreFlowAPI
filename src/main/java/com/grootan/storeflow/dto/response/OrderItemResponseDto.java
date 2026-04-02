package com.grootan.storeflow.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemResponseDto {
    private UUID id;
    private ProductResponseDto product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
