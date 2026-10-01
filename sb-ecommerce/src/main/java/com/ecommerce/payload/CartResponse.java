package com.ecommerce.payload;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
    Long cartId,
    BigDecimal totalPrice,
    List<CartItemResponse> products
) {
}
