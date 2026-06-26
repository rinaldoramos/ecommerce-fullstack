package com.ecommerce.controllers;

import com.ecommerce.models.Category;
import com.ecommerce.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<String> createCategory(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(category));
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@PathVariable Long categoryId, @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, category));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }
}
