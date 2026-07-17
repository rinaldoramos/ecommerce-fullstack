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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/categories/{categoryId}/product")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest, @PathVariable Long categoryId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(productRequest, categoryId));
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
        return ResponseEntity.status(HttpStatus.FOUND).body(productService.findByKeyword(keyword, pageable));
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
        @PathVariable Long productId,
        @Valid @RequestBody ProductRequest productRequest
    ) {
        return ResponseEntity.ok(productService.updateProduct(productId, productRequest));
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}








































