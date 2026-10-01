package com.ecommerce.services;

import com.ecommerce.repositories.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(readOnly = true)
    public long countByProduct(Long productId) {
        return cartItemRepository.countByProduct_ProductId(productId);
    }
}
