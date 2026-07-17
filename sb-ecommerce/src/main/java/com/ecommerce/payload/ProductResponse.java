package com.ecommerce.payload;

import java.math.BigDecimal;

public record ProductResponse(
    Long productId,
    String productName,
    String image,
    String description,
    Integer quantity,
    BigDecimal price,
    BigDecimal discount,
    BigDecimal specialPrice,
    CategoryResponse categoryResponse
) {
}