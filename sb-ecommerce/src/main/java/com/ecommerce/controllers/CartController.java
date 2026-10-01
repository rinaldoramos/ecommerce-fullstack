package com.ecommerce.controllers;

import com.ecommerce.common.PagedResponse;
import com.ecommerce.payload.CartResponse;
import com.ecommerce.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/carts")
    public ResponseEntity<PagedResponse<CartResponse>> getCarts(
        @PageableDefault(sort = "cartId", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(cartService.getAllCarts(pageable));
    }

    @GetMapping("/cart")
    public ResponseEntity<CartResponse> getUserCart(@AuthenticationPrincipal String username) {
        return ResponseEntity.ok(cartService.getUserCart(username));
    }

    @PostMapping("/carts/product/{productId}/quantity/{quantity}")
    public ResponseEntity<CartResponse> addProductToCart(
        @PathVariable Long productId,
        @PathVariable Integer quantity,
        @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(cartService.addingProductToCart(productId, quantity, username));
    }

    @PutMapping("/carts/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartResponse> updateProductQuantityInCart(
        @PathVariable Long productId,
        @PathVariable String operation,
        @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(cartService.updateProductQuantityInCart(productId, operation , username));
    }

    @DeleteMapping("/carts/products/{productId}")
    public ResponseEntity<CartResponse> deleteProductFromCart(
        @PathVariable Long productId,
        @AuthenticationPrincipal String username) {
        return ResponseEntity.ok(cartService.deleteProductFromCart(productId, username));
    }
}
