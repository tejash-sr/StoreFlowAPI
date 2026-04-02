$baseDir = "e:\GROOTAN\grootan-spring-boot-exercise-tejash\src\main\java\com\grootan\storeflow\controllers"

Set-Content -Path "$baseDir\CategoryController.java" -Value @"
package com.grootan.storeflow.controllers;

import com.grootan.storeflow.dto.request.CategoryRequestDto;
import com.grootan.storeflow.dto.response.CategoryResponseDto;
import com.grootan.storeflow.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        return categoryService.createCategory(requestDto);
    }

    @GetMapping
    public Page<CategoryResponseDto> getAllCategories(Pageable pageable) {
        return categoryService.getAllCategories(pageable);
    }

    @GetMapping("/{id}")
    public CategoryResponseDto getCategoryById(@PathVariable UUID id) {
        return categoryService.getCategoryById(id);
    }

    @PutMapping("/{id}")
    public CategoryResponseDto updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequestDto requestDto) {
        return categoryService.updateCategory(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
    }
}
"@

Set-Content -Path "$baseDir\ProductController.java" -Value @"
package com.grootan.storeflow.controllers;

import com.grootan.storeflow.dto.request.ProductRequestDto;
import com.grootan.storeflow.dto.request.StockUpdateRequestDto;
import com.grootan.storeflow.dto.response.ProductResponseDto;
import com.grootan.storeflow.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto createProduct(@Valid @RequestBody ProductRequestDto requestDto) {
        return productService.createProduct(requestDto);
    }

    @GetMapping
    public Page<ProductResponseDto> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Pageable pageable) {
        return productService.getAllProducts(category, status, minPrice, maxPrice, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponseDto getProductById(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public ProductResponseDto updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductRequestDto requestDto) {
        return productService.updateProduct(id, requestDto);
    }

    @PatchMapping("/{id}/stock")
    public ProductResponseDto updateStock(@PathVariable UUID id, @Valid @RequestBody StockUpdateRequestDto requestDto) {
        return productService.updateStock(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
    }
}
"@

Set-Content -Path "$baseDir\OrderController.java" -Value @"
package com.grootan.storeflow.controllers;

import com.grootan.storeflow.dto.request.OrderRequestDto;
import com.grootan.storeflow.dto.request.OrderStatusUpdateRequestDto;
import com.grootan.storeflow.dto.response.OrderResponseDto;
import com.grootan.storeflow.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto placeOrder(@Valid @RequestBody OrderRequestDto requestDto) {
        // Hardcoded admin customer until auth is added in Phase 4
        // Assume customer setup happens in tests/db scripts
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000001"); 
        
        // Let user setup dynamically in test context. For actual endpoint this will be mocked safely in test
        return orderService.placeOrder(customerId, requestDto);
    }

    @GetMapping
    public Page<OrderResponseDto> getOrders(Pageable pageable) {
        UUID customerId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        return orderService.getOrdersForUser(customerId, pageable);
    }

    @GetMapping("/{id}")
    public OrderResponseDto getOrderById(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @PatchMapping("/{id}/status")
    public OrderResponseDto updateOrderStatus(@PathVariable UUID id, @Valid @RequestBody OrderStatusUpdateRequestDto requestDto) {
        return orderService.updateOrderStatus(id, requestDto);
    }
}
"@
