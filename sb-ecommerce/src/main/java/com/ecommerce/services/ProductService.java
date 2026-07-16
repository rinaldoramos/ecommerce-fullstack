package com.ecommerce.services;

import com.ecommerce.common.PagedResponse;
import com.ecommerce.payload.ProductRequest;
import com.ecommerce.payload.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

public interface ProductService {
    ProductResponse saveProduct(ProductRequest productRequest, Long categoryId);

    @EntityGraph(attributePaths = "category")
    PagedResponse<ProductResponse> findAllProducts(Pageable pageable);
}
