package com.ecommerce.mappers;

import com.ecommerce.models.Category;
import com.ecommerce.models.Product;
import com.ecommerce.payload.ProductRequest;
import com.ecommerce.payload.ProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductRequest productRequest, Category category);

    ProductResponse toResponse(Product product);
}
