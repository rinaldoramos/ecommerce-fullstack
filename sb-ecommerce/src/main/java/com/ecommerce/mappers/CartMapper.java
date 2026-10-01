package com.ecommerce.mappers;

import com.ecommerce.models.Cart;
import com.ecommerce.models.CartItem;
import com.ecommerce.models.Product;
import com.ecommerce.payload.CartItemResponse;
import com.ecommerce.payload.CartResponse;
import com.ecommerce.payload.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;


@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "cartItems", target = "products")
    CartResponse toCartResponse(Cart cart);

    @Mapping(
        target = "productResponse",
        expression = "java(toCartProductResponse(cartItem.getProduct(), cartItem.getQuantity(), cartItem.getProductPrice(), cartItem.getDiscount(), cartItem.getProductSpecialPrice()))")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    @Mapping(target = "price", source = "cartPrice")
    @Mapping(target = "specialPrice", source = "cartSpecialPrice")
    @Mapping(target = "discount", source = "cartDiscount")
    @Mapping(target = "quantity", source = "cartQuantity")
    @Mapping(target = "categoryResponse", source = "product.category")
    ProductResponse toCartProductResponse(Product product, Integer cartQuantity, BigDecimal cartPrice, BigDecimal cartDiscount, BigDecimal cartSpecialPrice);
}