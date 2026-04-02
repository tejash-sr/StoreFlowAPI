package com.grootan.storeflow.service;

import com.grootan.storeflow.dto.request.ProductRequestDto;
import com.grootan.storeflow.dto.response.ProductResponseDto;
import com.grootan.storeflow.enums.ProductStatus;
import com.grootan.storeflow.exceptions.ResourceNotFoundException;
import com.grootan.storeflow.mapper.ProductMapper;
import com.grootan.storeflow.models.Category;
import com.grootan.storeflow.models.Product;
import com.grootan.storeflow.repository.CategoryRepository;
import com.grootan.storeflow.repository.ProductRepository;
import com.grootan.storeflow.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;
    private ProductRequestDto requestDto;
    private ProductResponseDto responseDto;
    private UUID categoryId = UUID.randomUUID();
    private UUID productId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");

        product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setStock(10);
        product.setCategory(category);
        product.setStatus(ProductStatus.ACTIVE);

        requestDto = new ProductRequestDto();
        requestDto.setName("Test Product");
        requestDto.setCategoryId(categoryId);
        requestDto.setPrice(BigDecimal.TEN);
        requestDto.setStock(10);
        
        responseDto = new ProductResponseDto();
        responseDto.setId(productId);
        responseDto.setName("Test Product");
    }

    @Test
    void createProduct_success() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(any(Product.class))).thenReturn(responseDto);

        ProductResponseDto result = productService.createProduct(requestDto);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_categoryNotFound_throwsException() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(requestDto));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_success_softDeletes() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.deleteProduct(productId);

        assertEquals(ProductStatus.DISCONTINUED, product.getStatus());
        assertNotNull(product.getDeletedAt());
        verify(productRepository).save(product);
    }
}
