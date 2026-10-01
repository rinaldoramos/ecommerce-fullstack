package com.ecommerce.repositories;

import com.ecommerce.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    long countByProduct_ProductId(Long productId);
}
