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
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
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
