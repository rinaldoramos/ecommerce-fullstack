package com.ecommerce.payload;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
    @NotBlank(message = "Category name is required")
    @Size(min = 5, message = "Category name must be at least 5 characters")
    String categoryName
) {}
