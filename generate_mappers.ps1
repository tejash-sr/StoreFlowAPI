$baseDir = "e:\GROOTAN\grootan-spring-boot-exercise-tejash\src\main\java\com\grootan\storeflow"
New-Item -ItemType Directory -Force -Path "$baseDir\mapper"
New-Item -ItemType Directory -Force -Path "$baseDir\service\impl"
New-Item -ItemType Directory -Force -Path "$baseDir\controllers"

# Mappers
Set-Content -Path "$baseDir\mapper\CategoryMapper.java" -Value @"
package com.grootan.storeflow.mapper;

import com.grootan.storeflow.dto.response.CategoryResponseDto;
import com.grootan.storeflow.models.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponseDto toDto(Category category) {
        if (category == null) return null;
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setStatus(category.getStatus());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
        }
        return dto;
    }
}
"@

Set-Content -Path "$baseDir\mapper\ProductMapper.java" -Value @"
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
"@

Set-Content -Path "$baseDir\mapper\OrderMapper.java" -Value @"
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
        
        if (order.getOrderItems() != null) {
            dto.setItems(order.getOrderItems().stream().map(this::toItemDto).collect(Collectors.toList()));
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
"@
