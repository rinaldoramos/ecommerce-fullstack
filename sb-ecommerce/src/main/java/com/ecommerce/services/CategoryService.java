package com.ecommerce.services;

import com.ecommerce.models.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();
    Category createCategory(Category category);
    String deleteCategory(Long categoryId);
    String updateCategory(Long categoryId, Category category);
}
