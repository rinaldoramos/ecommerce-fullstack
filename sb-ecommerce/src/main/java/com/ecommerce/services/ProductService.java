package com.ecommerce.services;

import com.ecommerce.common.PagedResponse;
import com.ecommerce.payload.ProductRequest;
import com.ecommerce.payload.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ProductResponse saveProduct(ProductRequest productRequest, Long categoryId);

    @EntityGraph(attributePaths = "category")
    PagedResponse<ProductResponse> findAllProducts(Pageable pageable);

    @EntityGraph(attributePaths = "category")
    PagedResponse<ProductResponse> findProductsByCategory(Long categoryId, Pageable pageable);

    PagedResponse<ProductResponse> findByKeyword(String keyword, Pageable pageable);

    ProductResponse updateProduct(Long productId, ProductRequest productRequest);

    void deleteProduct(Long productId);

    ProductResponse updateProductImage(Long productId, MultipartFile image);
}
