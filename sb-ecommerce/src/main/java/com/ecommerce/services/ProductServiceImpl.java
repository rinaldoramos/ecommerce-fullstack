package com.ecommerce.services;

import com.ecommerce.common.PagedResponse;
import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.mappers.ProductMapper;
import com.ecommerce.models.AppRole;
import com.ecommerce.models.Category;
import com.ecommerce.models.Product;
import com.ecommerce.models.User;
import com.ecommerce.payload.ProductRequest;
import com.ecommerce.payload.ProductResponse;
import com.ecommerce.repositories.CategoryRepository;
import com.ecommerce.repositories.ProductRepository;
import com.ecommerce.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final FileService fileService;
    private final UserRepository userRepository;
    private final CartItemService cartItemService;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> findAllProducts(Pageable pageable) {
        Page<ProductResponse> productResponsePage =
            productRepository.findAll(pageable).map(productMapper::toResponse);

        return PagedResponse.from(productResponsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> findProductsByCategory(Long categoryId, Pageable pageable) {
        Category categoryFound = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId, HttpStatus.NOT_FOUND));

        Page<ProductResponse> productResponsePage = productRepository.findAllByCategory(pageable, categoryFound)
            .map(productMapper::toResponse);

        return PagedResponse.from(productResponsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> findByKeyword(String keyword, Pageable pageable) {
        Page<ProductResponse> productResponsePage = productRepository.findByProductNameContainingIgnoreCase(keyword, pageable)
            .map(productMapper::toResponse);

        return PagedResponse.from(productResponsePage);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ProductResponse updateProduct(Long productId, ProductRequest productRequest, String username) {
        Product productFromDB = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId, HttpStatus.NOT_FOUND));

        checkCurrentUserCanMakeChanges(productFromDB, username);

        productMapper.updateProduct(productRequest, productFromDB);

        Product updatedProduct = productRepository.saveAndFlush(productFromDB);

        return productMapper.toResponse(updatedProduct);
    }

    private void checkCurrentUserCanMakeChanges(Product productFromDB, String username) {
        boolean currentOwner = productFromDB.getSeller().getUsername().equals(username);

        if (!currentOwner && !isAdmin())
            throw new AccessDeniedException("You are not allowed to modify this product");
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> AppRole.ROLE_ADMIN.name().equals(a.getAuthority()));
    }


    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public void deleteProduct(Long productId, String username) {

        Product productToBeDeleted = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId, HttpStatus.NOT_FOUND));

        checkCurrentUserCanMakeChanges(productToBeDeleted, username);

        long inCarts = cartItemService.countByProduct(productId);

        if (inCarts > 0) {
            throw new APIException(
                "Product is in " + inCarts + " active cart(s) and cannot be deleted", HttpStatus.CONFLICT);
        }

        reconcileImageAfterTransaction(productToBeDeleted.getImage(), null);

        productRepository.delete(productToBeDeleted);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ProductResponse updateProductImage(Long productId, MultipartFile image, String username) {
        if (image == null || image.isEmpty()) {
            throw new APIException("Image is required", HttpStatus.BAD_REQUEST);
        }

        Product productFound = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId, HttpStatus.NOT_FOUND));

        checkCurrentUserCanMakeChanges(productFound, username);

        String oldImage = productFound.getImage();

        String newImage = fileService.saveImage(image);

        productFound.setImage(newImage);

        reconcileImageAfterTransaction(oldImage, newImage);

        Product updatedProductWithImage = productRepository.save(productFound);

        return productMapper.toResponse(updatedProductWithImage);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ProductResponse saveProduct(ProductRequest productRequest, Long categoryId, String username) {
        Category categoryFound = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId, HttpStatus.NOT_FOUND));

        User sellerFound  = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username, HttpStatus.NOT_FOUND));

        Product productToBeSaved = productMapper.toProduct(productRequest, categoryFound, sellerFound );

        Product savedProduct = productRepository.save(productToBeSaved);

        return productMapper.toResponse(savedProduct);
    }

    private void reconcileImageAfterTransaction(String oldImage, String newImage) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                fileService.deleteOldImage(status == STATUS_COMMITTED ? oldImage : newImage);
            }
        });
    }
}
