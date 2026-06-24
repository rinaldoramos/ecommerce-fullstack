package com.ecommerce.service;

import com.ecommerce.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final List<Category> categories = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public String createCategory(Category category) {
        category.setCategoryId(nextId.getAndIncrement());
        categories.add(category);
        return "Added category: " + category.getCategoryName();
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category categoryFound = categories.stream()
            .filter(category -> category.getCategoryId().equals(categoryId))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        categories.remove(categoryFound);
        return "Deleted category: " + categoryFound.getCategoryName();
    }

    @Override
    public String updateCategory(Long categoryId, Category category) {
        Optional<Category> optionalCategory = categories.stream()
            .filter(cat -> cat.getCategoryId().equals(categoryId))
            .findFirst()
            .map(cat -> {
                cat.setCategoryName(category.getCategoryName());
                return cat;
            });

        if (optionalCategory.isPresent()) {
            return "Updated category: " + optionalCategory.get().getCategoryName();
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category with ID :: " + categoryId + " not found");
        }
    }
}
