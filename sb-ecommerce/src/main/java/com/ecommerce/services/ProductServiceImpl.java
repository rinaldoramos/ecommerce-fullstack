package com.ecommerce.services;

import com.ecommerce.common.PagedResponse;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.mappers.ProductMapper;
import com.ecommerce.models.Category;
import com.ecommerce.models.Product;
import com.ecommerce.payload.ProductRequest;
import com.ecommerce.payload.ProductResponse;
import com.ecommerce.repositories.CategoryRepository;
import com.ecommerce.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    @Override
    public PagedResponse<ProductResponse> findAllProducts(Pageable pageable) {
        Page<ProductResponse> productResponsePage =
            productRepository.findAll(pageable).map(productMapper::toResponse);

        return PagedResponse.from(productResponsePage);
    }

    @Transactional(readOnly = true)
    @Override
    public PagedResponse<ProductResponse> findProductsByCategory(Long categoryId, Pageable pageable) {
        Category categoryFound = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Page<ProductResponse> productResponsePage = productRepository.findAllByCategory(pageable, categoryFound)
            .map(productMapper::toResponse);

        return PagedResponse.from(productResponsePage);
    }

    @Override
    public PagedResponse<ProductResponse> findByKeyword(String keyword, Pageable pageable) {
        Page<ProductResponse> productResponsePage = productRepository.findByProductNameContainingIgnoreCase(keyword, pageable)
            .map(productMapper::toResponse);

        return PagedResponse.from(productResponsePage);
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest productRequest) {
        Product productFromDB = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        productMapper.updateProduct(productRequest, productFromDB);

        Product updatedProduct = productRepository.saveAndFlush(productFromDB);

        return productMapper.toResponse(updatedProduct);
    }

    @Transactional
    @Override
    public void deleteProduct(Long productId) {
        if (productId == null)
            return;

        Product productToBeDeleted = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        productRepository.delete(productToBeDeleted);
    }

    @Transactional
    @Override
    public ProductResponse saveProduct(ProductRequest productRequest, Long categoryId) {
        Category categoryFound = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Product productToBeSaved = productMapper.toProduct(productRequest, categoryFound);

        Product savedProduct = productRepository.save(productToBeSaved);

        return productMapper.toResponse(savedProduct);
    }
}
