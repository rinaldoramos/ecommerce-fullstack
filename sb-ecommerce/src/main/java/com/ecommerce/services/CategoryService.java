package com.ecommerce.services;

import com.ecommerce.common.PagedResponse;
import com.ecommerce.models.Category;
import com.ecommerce.payload.CategoryRequest;
import com.ecommerce.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {

    PagedResponse<CategoryResponse> getAllCategories();
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    void deleteCategory(Long categoryId);
    CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest);
}
