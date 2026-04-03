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
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productService.getAllProducts(pageable);
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
