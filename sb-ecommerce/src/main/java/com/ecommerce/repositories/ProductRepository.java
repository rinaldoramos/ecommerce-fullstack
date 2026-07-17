package com.ecommerce.repositories;

import com.ecommerce.models.Category;
import com.ecommerce.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByCategory(Pageable pageable, Category category);

    Page<Product> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);
}
