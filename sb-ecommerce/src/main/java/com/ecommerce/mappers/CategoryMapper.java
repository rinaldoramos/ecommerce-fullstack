package com.ecommerce.mappers;

import com.ecommerce.models.Category;
import com.ecommerce.payload.CategoryRequest;
import com.ecommerce.payload.CategoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);

    Category toCategory(CategoryRequest categoryRequest);
}
