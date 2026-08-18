package com.guuh.transaction_service.business.mapper;

import com.guuh.transaction_service.business.dto.request.CategoryRequestDto;
import com.guuh.transaction_service.business.dto.response.CategoryResponseDto;
import com.guuh.transaction_service.infrastructure.entity.Category;
import com.guuh.transaction_service.infrastructure.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryMapperTest {

    CategoryMapper categoryMapper;
    Category category;
    Category category1;
    User user;
    List<CategoryResponseDto> dtoList;
    List<Category> list;
    CategoryRequestDto categoryRequestDto;

    @BeforeEach
    public void setup() {
        categoryMapper = Mappers.getMapper(CategoryMapper.class);
        user = User.builder()
                .id(1L)
                .name("Gustavo")
                .email("EMAIL_REMOVIDO")
                .password("SENHA_REMOVIDA")
                .build();

        category = Category.builder()
                .id(1L)
                .name("Salário")
                .user(user)
                .build();

        category1 = Category.builder()
                .id(2L)
                .name("Carro")
                .user(user)
                .build();

        categoryRequestDto = new CategoryRequestDto("Alimentação");

        list = List.of(category, category1);

        dtoList = List.of(
                new CategoryResponseDto(1L, "Salário"),
                new CategoryResponseDto(2L, "Carro")
        );

    }

    @Test
    void shouldMapCategoryToCategoryResponseDto() {
        CategoryResponseDto dto = categoryMapper.toCategoryDto(category);
        assertEquals(category.getId(), dto.id());
        assertEquals(category.getName(), dto.name());
    }

    @Test
    void shouldMapCategoryRequestToCategory() {
        Category entity = categoryMapper.toCategory(categoryRequestDto);
        assertEquals(categoryRequestDto.name(), entity.getName());
    }

    @Test
    void shouldMapCategoryListToDtoList() {
        List<CategoryResponseDto> actual = categoryMapper.toCategoryDtoList(list);
        assertEquals(dtoList, actual);
    }

}
