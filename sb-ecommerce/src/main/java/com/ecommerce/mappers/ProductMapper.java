package com.ecommerce.mappers;

import com.ecommerce.models.Category;
import com.ecommerce.models.Product;
import com.ecommerce.payload.ProductRequest;
import com.ecommerce.payload.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductRequest productRequest, Category category);

    @Mapping(source = "category", target = "categoryResponse")
    ProductResponse toResponse(Product product);

    void updateProduct(ProductRequest productRequest, @MappingTarget Product productFromDB);
}