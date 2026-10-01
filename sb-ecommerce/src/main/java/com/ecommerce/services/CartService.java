package com.ecommerce.services;

import com.ecommerce.common.PagedResponse;
import com.ecommerce.payload.CartResponse;
import org.springframework.data.domain.Pageable;

public interface CartService {
    CartResponse addingProductToCart(Long productId, Integer quantity, String username);

    PagedResponse<CartResponse> getAllCarts(Pageable pageable);

    CartResponse getUserCart(String username);

    CartResponse updateProductQuantityInCart(Long productId, String operation, String username);

    CartResponse deleteProductFromCart(Long productId, String username);
}
