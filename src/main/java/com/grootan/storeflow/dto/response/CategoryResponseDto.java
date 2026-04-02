package com.grootan.storeflow.dto.response;

import lombok.Data;
import java.util.UUID;
import java.time.OffsetDateTime;

@Data
public class CategoryResponseDto {
    private UUID id;
    private String name;
    private String description;
    private UUID parentId;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
