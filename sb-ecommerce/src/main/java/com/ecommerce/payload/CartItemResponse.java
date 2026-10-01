package com.ecommerce.payload;

public record CartItemResponse(
    Long cartItemId,
    ProductResponse productResponse
) {
}
