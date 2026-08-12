package com.guuh.transaction_service.business.mapper;

import com.guuh.transaction_service.business.dto.request.CategoryRequestDto;
import com.guuh.transaction_service.business.dto.response.CategoryResponseDto;
import com.guuh.transaction_service.infrastructure.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(source = "id", target = "id")
    CategoryResponseDto toCategoryDto(Category category);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "user", ignore = true)
    Category toCategory(CategoryRequestDto dto);

    List<CategoryResponseDto> toCategoryDtoList(List<Category> categoryList);

}
