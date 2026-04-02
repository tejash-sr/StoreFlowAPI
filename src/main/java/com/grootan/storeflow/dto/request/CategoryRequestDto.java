package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.UUID;

@Data
public class CategoryRequestDto {
    @NotBlank(message = "Category name is required")
    private String name;
    
    private String description;
    
    private UUID parentId;
}
