package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

import com.grootan.storeflow.models.Category;
import com.grootan.storeflow.validation.ExistsInDatabase;
import com.grootan.storeflow.validation.ValidSku;

@Data
public class ProductRequestDto {
    @NotBlank(message = "Product name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "SKU is required")
    @ValidSku
    private String sku;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
    
    @NotNull(message = "Category ID is required")
    @ExistsInDatabase(entityClass = Category.class, message = "Category does not exist")
    private UUID categoryId;
    
    private String imageUrl;
}
