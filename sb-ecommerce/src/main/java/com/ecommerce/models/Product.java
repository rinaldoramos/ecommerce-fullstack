package com.ecommerce.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, length = 150)
    private String productName;

    @Column(nullable = false, length = 512)
    private String image;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discount;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal specialPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @PrePersist
    @PreUpdate
    private void calculateSpecialPrice() {
        if (this.price == null) {
            this.specialPrice = BigDecimal.ZERO;
            return;
        }

        if (this.discount == null || this.discount.compareTo(BigDecimal.ZERO) <= 0) {
            this.specialPrice = this.price.setScale(2, RoundingMode.HALF_UP);
            return;
        }

        BigDecimal discountedPrice = this.price.multiply(
            this.discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        );
        this.specialPrice = this.price.subtract(discountedPrice).setScale(2, RoundingMode.HALF_UP);
    }
}