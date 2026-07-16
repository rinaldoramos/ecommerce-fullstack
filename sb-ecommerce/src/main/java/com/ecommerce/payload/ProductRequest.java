package com.ecommerce.payload;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank(message = "Product name is required")
    @Size(min = 5, message = "Product name must be at least 5 characters")
    String productName,

    @NotBlank(message = "Product image URL is required")
    @Size(min = 5, message = "Product image URL must be at least 5 characters")
    String image,

    @NotBlank(message = "Product description is required")
    @Size(min = 5, message = "Product description must be at least 5 characters")
    String description,

    @NotNull(message = "Product quantity is required")
    @Min(value = 0, message = "Product quantity cannot be negative")
    Integer quantity,

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    BigDecimal price,

    @NotNull(message = "Discount is required")
    @DecimalMin(value = "0.00", message = "Discount cannot be negative")
    @DecimalMax(value = "100.00", message = "Discount cannot exceed 100%")
    BigDecimal discount
) {
}
