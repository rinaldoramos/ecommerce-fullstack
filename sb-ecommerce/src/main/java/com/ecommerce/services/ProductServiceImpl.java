package com.ecommerce.services;

import com.ecommerce.common.PagedResponse;
import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.mappers.ProductMapper;
import com.ecommerce.models.Category;
import com.ecommerce.models.Product;
import com.ecommerce.payload.ProductRequest;
import com.ecommerce.payload.ProductResponse;
import com.ecommerce.repositories.CategoryRepository;
import com.ecommerce.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final FileService fileService;

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
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId, HttpStatus.NOT_FOUND));

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
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId, HttpStatus.NOT_FOUND));

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
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId, HttpStatus.NOT_FOUND));

        productRepository.delete(productToBeDeleted);
    }

    @Transactional
    @Override
    public ProductResponse updateProductImage(Long productId, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new APIException("Image is required", HttpStatus.BAD_REQUEST);
        }

        Product productFound = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId, HttpStatus.NOT_FOUND));

        String oldImage = productFound.getImage();

        String newImage = fileService.saveImage(image);

        productFound.setImage(newImage);

        fileService.deleteOldImage(oldImage);

        Product updatedProductWithImage = productRepository.save(productFound);

        return productMapper.toResponse(updatedProductWithImage);
    }

    @Transactional
    @Override
    public ProductResponse saveProduct(ProductRequest productRequest, Long categoryId) {
        Category categoryFound = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId, HttpStatus.NOT_FOUND));

        Product productToBeSaved = productMapper.toProduct(productRequest, categoryFound);

        Product savedProduct = productRepository.save(productToBeSaved);

        return productMapper.toResponse(savedProduct);
    }
}
