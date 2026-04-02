package com.grootan.storeflow.mapper;

import com.grootan.storeflow.dto.response.ProductResponseDto;
import com.grootan.storeflow.models.Product;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final CategoryMapper categoryMapper;
    
    public ProductResponseDto toDto(Product product) {
        if (product == null) return null;
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setSku(product.getSku());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        if (product.getStatus() != null) {
            dto.setStatus(product.getStatus().name());
        }
        dto.setCategory(categoryMapper.toDto(product.getCategory()));
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}
