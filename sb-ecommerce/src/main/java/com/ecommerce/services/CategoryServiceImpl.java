package com.ecommerce.services;

import com.ecommerce.models.Category;
import com.ecommerce.repositories.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public String createCategory(Category category) {
        categoryRepository.save(category);
        return "Added category: " + category.getCategoryName();
    }

    @Override
    @Transactional
    public String deleteCategory(Long categoryId) {
        Category categoryToBeDeleted = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        categoryRepository.delete(categoryToBeDeleted);
        return "Deleted category: " + categoryId;
    }

    @Override
    @Transactional
    public String updateCategory(Long categoryId, Category category) {
        Category categoryFound = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        categoryFound.setCategoryName(category.getCategoryName());

        categoryRepository.save(categoryFound);
        return "Updated category: " + categoryFound.getCategoryName();
    }
}
