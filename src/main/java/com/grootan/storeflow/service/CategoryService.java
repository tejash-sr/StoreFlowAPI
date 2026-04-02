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
