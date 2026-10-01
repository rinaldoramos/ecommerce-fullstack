package com.ecommerce.payload;

public record CartRequest(
    Long productId,
    Integer quantity
) {
}
