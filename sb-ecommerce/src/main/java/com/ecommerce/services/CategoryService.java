package com.ecommerce.services;

import com.ecommerce.common.PagedResponse;
import com.ecommerce.payload.CategoryRequest;
import com.ecommerce.payload.CategoryResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    PagedResponse<CategoryResponse> getAllCategories(Pageable pageable);
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    void deleteCategory(Long categoryId);
    CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest);
}
