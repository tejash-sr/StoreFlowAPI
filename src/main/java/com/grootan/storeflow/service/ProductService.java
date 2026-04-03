package com.grootan.storeflow.service;

import com.grootan.storeflow.dto.request.ProductRequestDto;
import com.grootan.storeflow.dto.request.StockUpdateRequestDto;
import com.grootan.storeflow.dto.response.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto requestDto);
    Page<ProductResponseDto> getAllProducts(Pageable pageable);
    ProductResponseDto getProductById(UUID id);
    ProductResponseDto updateProduct(UUID id, ProductRequestDto requestDto);
    ProductResponseDto updateStock(UUID id, StockUpdateRequestDto requestDto);
    void deleteProduct(UUID id);
    
    ProductResponseDto uploadProductImage(UUID id, org.springframework.web.multipart.MultipartFile file);
    org.springframework.core.io.Resource getProductImage(UUID id);
}
