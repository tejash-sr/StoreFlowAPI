$baseDir = "e:\GROOTAN\grootan-spring-boot-exercise-tejash\src\main\java\com\grootan\storeflow\service"

# Category Service
Set-Content -Path "$baseDir\CategoryService.java" -Value @"
package com.grootan.storeflow.service;

import com.grootan.storeflow.dto.request.CategoryRequestDto;
import com.grootan.storeflow.dto.response.CategoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto requestDto);
    Page<CategoryResponseDto> getAllCategories(Pageable pageable);
    CategoryResponseDto getCategoryById(UUID id);
    CategoryResponseDto updateCategory(UUID id, CategoryRequestDto requestDto);
    void deleteCategory(UUID id);
}
"@

Set-Content -Path "$baseDir\impl\CategoryServiceImpl.java" -Value @"
package com.grootan.storeflow.service.impl;

import com.grootan.storeflow.dto.request.CategoryRequestDto;
import com.grootan.storeflow.dto.response.CategoryResponseDto;
import com.grootan.storeflow.exceptions.ResourceNotFoundException;
import com.grootan.storeflow.mapper.CategoryMapper;
import com.grootan.storeflow.models.Category;
import com.grootan.storeflow.repository.CategoryRepository;
import com.grootan.storeflow.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        Category category = new Category();
        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());
        
        if (requestDto.getParentId() != null) {
            Category parent = categoryRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category not found with id: " + requestDto.getParentId()));
            category.setParent(parent);
        }
        
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponseDto> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(UUID id, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
                
        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());
        
        if (requestDto.getParentId() != null) {
            Category parent = categoryRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category not found with id: " + requestDto.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }
        
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setStatus("INACTIVE"); // soft delete logic based on status
        categoryRepository.save(category);
    }
}
"@

# Product Service
Set-Content -Path "$baseDir\ProductService.java" -Value @"
package com.grootan.storeflow.service;

import com.grootan.storeflow.dto.request.ProductRequestDto;
import com.grootan.storeflow.dto.request.StockUpdateRequestDto;
import com.grootan.storeflow.dto.response.ProductResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto requestDto);
    Page<ProductResponseDto> getAllProducts(String category, String status, Double minPrice, Double maxPrice, Pageable pageable);
    ProductResponseDto getProductById(UUID id);
    ProductResponseDto updateProduct(UUID id, ProductRequestDto requestDto);
    ProductResponseDto updateStock(UUID id, StockUpdateRequestDto requestDto);
    void deleteProduct(UUID id);
}
"@

Set-Content -Path "$baseDir\impl\ProductServiceImpl.java" -Value @"
package com.grootan.storeflow.service.impl;

import com.grootan.storeflow.dto.request.ProductRequestDto;
import com.grootan.storeflow.dto.request.StockUpdateRequestDto;
import com.grootan.storeflow.dto.response.ProductResponseDto;
import com.grootan.storeflow.enums.ProductStatus;
import com.grootan.storeflow.exceptions.ResourceNotFoundException;
import com.grootan.storeflow.mapper.ProductMapper;
import com.grootan.storeflow.models.Category;
import com.grootan.storeflow.models.Product;
import com.grootan.storeflow.repository.CategoryRepository;
import com.grootan.storeflow.repository.ProductRepository;
import com.grootan.storeflow.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDto.getCategoryId()));

        Product product = new Product();
        product.setName(requestDto.getName());
        product.setDescription(requestDto.getDescription());
        product.setSku(requestDto.getSku());
        product.setPrice(requestDto.getPrice());
        product.setStock(requestDto.getStock());
        product.setCategory(category);
        product.setImageUrl(requestDto.getImageUrl());
        
        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProducts(String category, String status, Double minPrice, Double maxPrice, Pageable pageable) {
        // Simple findAll for now; advanced specification querying in Phase 7
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(UUID id, ProductRequestDto requestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + requestDto.getCategoryId()));

        product.setName(requestDto.getName());
        product.setDescription(requestDto.getDescription());
        product.setSku(requestDto.getSku());
        product.setPrice(requestDto.getPrice());
        product.setStock(requestDto.getStock());
        product.setCategory(category);
        product.setImageUrl(requestDto.getImageUrl());

        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductResponseDto updateStock(UUID id, StockUpdateRequestDto requestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        int newStock = product.getStock() + requestDto.getAmount();
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        product.setStock(newStock);
        
        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setStatus(ProductStatus.DISCONTINUED);
        product.setDeletedAt(OffsetDateTime.now());
        productRepository.save(product);
    }
}
"@
