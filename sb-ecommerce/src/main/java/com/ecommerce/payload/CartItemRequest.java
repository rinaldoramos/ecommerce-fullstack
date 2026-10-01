package com.ecommerce.payload;

import java.math.BigDecimal;

public record CartItemRequest(
    CartRequest cartRequest,
    ProductRequest productRequest,
    Integer quantity,
    BigDecimal totalPrice,
    BigDecimal discount
) {
}
