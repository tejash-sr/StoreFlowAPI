package com.grootan.storeflow.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.time.OffsetDateTime;

@Data
public class OrderResponseDto {
    private UUID id;
    private String referenceNumber;
    private UUID customerId;
    private String status;
    private String shippingStreet;
    private String shippingCity;
    private String shippingCountry;
    private String shippingPostalCode;
    private BigDecimal totalAmount;
    private List<OrderItemResponseDto> items;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
