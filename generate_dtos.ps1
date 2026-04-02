$dtoDir = "e:\GROOTAN\grootan-spring-boot-exercise-tejash\src\main\java\com\grootan\storeflow\dto"
New-Item -ItemType Directory -Force -Path "$dtoDir\request"
New-Item -ItemType Directory -Force -Path "$dtoDir\response"

# Category DTOs
Set-Content -Path "$dtoDir\request\CategoryRequestDto.java" -Value @"
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
"@

Set-Content -Path "$dtoDir\response\CategoryResponseDto.java" -Value @"
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
"@

# Product DTOs
Set-Content -Path "$dtoDir\request\ProductRequestDto.java" -Value @"
package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductRequestDto {
    @NotBlank(message = "Product name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "SKU is required")
    private String sku;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
    
    @NotNull(message = "Category ID is required")
    private UUID categoryId;
    
    private String imageUrl;
}
"@

Set-Content -Path "$dtoDir\response\ProductResponseDto.java" -Value @"
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
"@

# Order DTOs
Set-Content -Path "$dtoDir\request\OrderRequestDto.java" -Value @"
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
"@

Set-Content -Path "$dtoDir\request\OrderItemRequestDto.java" -Value @"
package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.util.UUID;

@Data
public class OrderItemRequestDto {
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}
"@

Set-Content -Path "$dtoDir\request\ShippingAddressDto.java" -Value @"
package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShippingAddressDto {
    @NotBlank(message = "Street is required")
    private String street;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "Country is required")
    private String country;
    
    @NotBlank(message = "Postal code is required")
    private String postalCode;
}
"@

Set-Content -Path "$dtoDir\response\OrderResponseDto.java" -Value @"
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
"@

Set-Content -Path "$dtoDir\response\OrderItemResponseDto.java" -Value @"
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
"@

# Update requests
Set-Content -Path "$dtoDir\request\StockUpdateRequestDto.java" -Value @"
package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockUpdateRequestDto {
    @NotNull(message = "Amount is required")
    private Integer amount; // positive to increment, negative to decrement
}
"@

Set-Content -Path "$dtoDir\request\OrderStatusUpdateRequestDto.java" -Value @"
package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderStatusUpdateRequestDto {
    @NotBlank(message = "Status is required")
    private String status;
}
"@
