package com.ecommerce.services;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.models.Category;
import com.ecommerce.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Category createCategory(Category category) {
        categoryRepository.findByCategoryNameIgnoreCase(category.getCategoryName())
            .ifPresent(categoryFound -> {
                throw new APIException("Category " + categoryFound.getCategoryName() + " already exist. Duplicates are not allowed!!");
            });

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public String deleteCategory(Long categoryId) {
        Category categoryToBeDeleted = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        categoryRepository.delete(categoryToBeDeleted);
        return "Deleted category: " + categoryId;
    }

    @Override
    @Transactional
    public String updateCategory(Long categoryId, Category category) {
        Category categoryFound = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        categoryFound.setCategoryName(category.getCategoryName());

        categoryRepository.save(categoryFound);
        return "Updated category: " + categoryFound.getCategoryName();
    }
}
