package com.grootan.storeflow.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;
import java.time.OffsetDateTime;

@Data
public class ProductResponseDto {
    private UUID id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Integer stock;
    private CategoryResponseDto category;
    private String imageUrl;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
