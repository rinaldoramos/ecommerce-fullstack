package com.ecommerce.services;

import com.ecommerce.common.PagedResponse;
import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.mappers.CategoryMapper;
import com.ecommerce.models.Category;
import com.ecommerce.payload.CategoryRequest;
import com.ecommerce.payload.CategoryResponse;
import com.ecommerce.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CategoryResponse> getAllCategories(Pageable pageable) {
        Page<CategoryResponse> categoryPage =
            categoryRepository.findAll(pageable).map(categoryMapper::toResponse);
        return PagedResponse.from(categoryPage);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        categoryRepository.findByCategoryNameIgnoreCase(categoryRequest.categoryName())
            .ifPresent(categoryFound -> {
                throw new APIException("Category " + categoryFound.getCategoryName() + " already exist. Duplicates are not allowed!!", HttpStatus.CONFLICT);
            });

        Category category = categoryMapper.toCategory(categoryRequest);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category categoryToBeDeleted = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId, HttpStatus.NOT_FOUND));

        categoryRepository.delete(categoryToBeDeleted);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        Category categoryFound = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId, HttpStatus.NOT_FOUND));

        categoryRepository.findByCategoryNameIgnoreCase(categoryRequest.categoryName())
            .filter(category -> !category.getCategoryId().equals(categoryId))
            .ifPresent(category -> {
                throw new APIException("Category " + category.getCategoryName() + " already exist. Duplicates are not allowed!!", HttpStatus.CONFLICT);
            });

        categoryFound.setCategoryName(categoryRequest.categoryName());

        Category savedCategory = categoryRepository.save(categoryFound);

        return categoryMapper.toResponse(savedCategory);
    }
}
