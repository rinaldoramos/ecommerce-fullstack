package com.ecommerce.services;

import com.ecommerce.common.PagedResponse;
import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.mappers.CategoryMapper;
import com.ecommerce.models.Category;
import com.ecommerce.payload.CategoryRequest;
import com.ecommerce.payload.CategoryResponse;
import com.ecommerce.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CategoryResponse> getAllCategories() {
        List<CategoryResponse> categoryResponseList = categoryRepository.findAll()
            .stream()
            .map(categoryMapper::toResponse)
            .toList();

        return new PagedResponse<>(categoryResponseList);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        categoryRepository.findByCategoryNameIgnoreCase(categoryRequest.categoryName())
            .ifPresent(categoryFound -> {
                throw new APIException("Category " + categoryFound.getCategoryName() + " already exist. Duplicates are not allowed!!");
            });

        Category category = categoryMapper.toCategory(categoryRequest);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category categoryToBeDeleted = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        categoryRepository.delete(categoryToBeDeleted);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        Category categoryFound = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        categoryFound.setCategoryName(categoryRequest.categoryName());

        Category savedCategory = categoryRepository.save(categoryFound);

        return categoryMapper.toResponse(savedCategory);
    }
}
