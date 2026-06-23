package com.ecommerce.controllers;

import com.ecommerce.model.Category;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final List<Category> categories = new java.util.ArrayList<>();

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categories;
    }

    @PostMapping("/categories")
    public String createCategory(@RequestBody Category category) {
        categories.add(category);
        return "Category created successfully";
    }
}
