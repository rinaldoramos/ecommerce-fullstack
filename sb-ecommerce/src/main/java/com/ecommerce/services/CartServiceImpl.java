package com.ecommerce.services;

import com.ecommerce.common.PagedResponse;
import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.mappers.CartMapper;
import com.ecommerce.models.Cart;
import com.ecommerce.models.CartItem;
import com.ecommerce.models.Product;
import com.ecommerce.models.User;
import com.ecommerce.payload.CartResponse;
import com.ecommerce.repositories.CartItemRepository;
import com.ecommerce.repositories.CartRepository;
import com.ecommerce.repositories.ProductRepository;
import com.ecommerce.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;


    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public PagedResponse<CartResponse> getAllCarts(Pageable pageable) {
        Page<CartResponse> allCarts = cartRepository.findAll(pageable)
            .map(cartMapper::toCartResponse);

        return PagedResponse.from(allCarts);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getUserCart(String username) {
        return cartRepository.findByUser_Username(username)
            .map(cartMapper::toCartResponse)
            .orElseGet(() -> new CartResponse(null, BigDecimal.ZERO, List.of()));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN', 'USER')")
    public CartResponse addingProductToCart(Long productId, Integer quantity, String username) {
        if (quantity == null || quantity <= 0)
            throw new APIException("Quantity must be greater than zero", HttpStatus.BAD_REQUEST);

        Product product = findProduct(productId, quantity);

        Cart currentCart = findOrCreateCart(username);

        addProductToCart(product, quantity, currentCart);

        recalculateTotal(currentCart);

        return cartMapper.toCartResponse(currentCart);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN', 'USER')")
    public CartResponse updateProductQuantityInCart(Long productId, String operation, String username) {

        int quantity = switch (operation.toLowerCase()) {
            case "add" -> 1;
            case "delete" -> -1;
            default -> throw new APIException("Invalid operation: " + operation + ". Use 'add' or 'delete'", HttpStatus.BAD_REQUEST);
        };

        Cart userCart = cartRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Cart", "username", username, HttpStatus.NOT_FOUND));

        CartItem cartItem = userCart.getCartItems().stream()
            .filter(item -> item.getProduct().getProductId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId, HttpStatus.NOT_FOUND));

        Product productFound = cartItem.getProduct();

        if (quantity > 0 && productFound.getQuantity() < quantity)
            throw new APIException("Product quantity is less than requested quantity", HttpStatus.CONFLICT);

        int newQuantity = cartItem.getQuantity() + quantity;
        productFound.setQuantity(productFound.getQuantity() - quantity);

        if (newQuantity <= 0) {
            userCart.removeCartItem(cartItem);
        } else {
            cartItem.setQuantity(newQuantity);
        }

        recalculateTotal(userCart);

        return cartMapper.toCartResponse(userCart);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN', 'USER')")
    public CartResponse deleteProductFromCart(Long productId, String username) {

        Cart userCart = cartRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Cart", "username", username, HttpStatus.NOT_FOUND));

        CartItem cartItem = userCart.getCartItems()
            .stream()
            .filter(item -> item.getProduct().getProductId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId, HttpStatus.NOT_FOUND));

        userCart.removeCartItem(cartItem);

        Product foundProduct = cartItem.getProduct();
        foundProduct.setQuantity(foundProduct.getQuantity() + cartItem.getQuantity());

        recalculateTotal(userCart);

        return cartMapper.toCartResponse(userCart);
    }

    private Product findProduct(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId, HttpStatus.NOT_FOUND));

        if (product.getQuantity() == 0)
            throw new APIException("Product is out of stock", HttpStatus.CONFLICT);

        if (product.getQuantity() < quantity)
            throw new APIException("Product quantity is less than requested quantity", HttpStatus.CONFLICT);

        return product;
    }

    private Cart findOrCreateCart(String username) {
        return cartRepository.findByUser_Username(username)
            .orElseGet(() -> createNewCart(username));
    }

    private Cart createNewCart(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Username", "username", username, HttpStatus.NOT_FOUND));
        return cartRepository.save(new Cart(user));
    }

    private void addProductToCart(Product product, Integer quantity, Cart currentCart) {
        boolean isProductInCart = currentCart.getCartItems().stream()
            .anyMatch(item -> item.getProduct().getProductId().equals(product.getProductId()));

        if (isProductInCart) {
            throw new APIException("Product " + product.getProductName() + " already exists in cart", HttpStatus.CONFLICT);
        }

        product.setQuantity(product.getQuantity() - quantity);

        CartItem cartItem = new CartItem();
        cartItem.setDiscount(product.getDiscount());
        cartItem.setQuantity(quantity);
        cartItem.setProductPrice(product.getPrice());
        cartItem.setProductSpecialPrice(product.getSpecialPrice());
        cartItem.setProduct(product);
        currentCart.addCartItem(cartItem);
    }

    private void recalculateTotal(Cart cart) {
        BigDecimal total = cart.getCartItems().stream()
            .map(item -> item.getProductSpecialPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(total);
    }
}
