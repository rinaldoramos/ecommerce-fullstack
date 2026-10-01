package com.ecommerce.controllers;

import com.ecommerce.common.PagedResponse;
import com.ecommerce.config.ProductConstant;
import com.ecommerce.payload.ProductRequest;
import com.ecommerce.payload.ProductResponse;
import com.ecommerce.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/categories/{categoryId}/product")
    public ResponseEntity<ProductResponse> createProduct(
        @Valid @RequestBody ProductRequest productRequest,
        @PathVariable Long categoryId,
        @AuthenticationPrincipal String username) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(productRequest, categoryId, username));
    }

    @GetMapping("/products")
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProducts(
        @PageableDefault(sort = ProductConstant.SORT, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(productService.findAllProducts(pageable));
    }

    @GetMapping("categories/{categoryId}/products")
    public ResponseEntity<PagedResponse<ProductResponse>> getProductsByCategory(
        @PathVariable Long categoryId,
        @PageableDefault(sort = ProductConstant.SORT, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(productService.findProductsByCategory(categoryId, pageable));
    }

    @GetMapping("/products/keyword/{keyword}")
    public ResponseEntity<PagedResponse<ProductResponse>> getProductsByKeyword(
        @PathVariable String keyword,
        @PageableDefault(sort = ProductConstant.SORT, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok().body(productService.findByKeyword(keyword, pageable));
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
        @PathVariable Long productId,
        @Valid @RequestBody ProductRequest productRequest,
        @AuthenticationPrincipal String username
    ) {
        return ResponseEntity.ok(productService.updateProduct(productId, productRequest, username));
    }

    @PutMapping("/product/{productId}/image")
    public ResponseEntity<ProductResponse> updateProductImage(
        @PathVariable Long productId,
        MultipartFile image,
        @AuthenticationPrincipal String username
    ) {
        return ResponseEntity.ok(productService.updateProductImage(productId, image, username));
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable Long productId,
        @AuthenticationPrincipal String username) {
        productService.deleteProduct(productId, username);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}








































