package com.guuh.transaction_service.business;

import com.guuh.transaction_service.api.dto.request.CategoryRequestDto;
import com.guuh.transaction_service.api.dto.request.CategoryRequestDtoFixture;
import com.guuh.transaction_service.api.dto.response.CategoryResponseDto;
import com.guuh.transaction_service.api.dto.response.CategoryResponseDtoFixture;
import com.guuh.transaction_service.api.mapper.CategoryMapper;
import com.guuh.transaction_service.infrastructure.entity.Category;
import com.guuh.transaction_service.infrastructure.entity.User;
import com.guuh.transaction_service.infrastructure.exceptions.CategoryAlreadyExistsException;
import com.guuh.transaction_service.infrastructure.exceptions.CategoryNotFoundException;
import com.guuh.transaction_service.infrastructure.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper mapper;
    @Mock
    private UserService userService;
    @Mock
    private Category category1;
    @Mock
    private Category category2;
    @Mock
    private List<Category> list;
    @Mock
    private CategoryResponseDto response;
    @Mock
    private List<CategoryResponseDto> dtoList;
    @Mock
    private CategoryRequestDto request;
    @Mock
    private User user;

    @BeforeEach
    void setup(){
        user = User.builder()
                .id(1L)
                .name("Gustavo")
                .email("teste@gmail.com")
                .password("teste123")
                .build();

        category1 = Category.builder()
                .id(1L)
                .name("Seguro")
                .user(user)
                .build();

        category2 = Category.builder()
                .id(2L)
                .name("Alimentação")
                .user(user)
                .build();

        response = CategoryResponseDtoFixture.build(
                1L,
                "Seguro"
        );

        request = CategoryRequestDtoFixture.build(
                "Seguro"
        );

        list = List.of(category1, category2);

        dtoList = List.of(
                CategoryResponseDtoFixture.build(1L, "Seguro"),
                CategoryResponseDtoFixture.build(2L, "Alimentação")
        );
    }

    @Test
    void shouldThrowWhenCategoryNameAlreadyExists(){
        when(categoryRepository.existsByNameIgnoreCaseAndUserId(request.name(), user.getId()))
                .thenReturn(true);
        when(userService.getLoggedUser()).thenReturn(user);

        CategoryAlreadyExistsException exception = assertThrows(
                CategoryAlreadyExistsException.class,
                () -> categoryService.validateCategoryUniqueness(request.name())
        );
        assertEquals("Essa categoria ja foi registrada!", exception.getMessage());
    }

    @Test
    void shouldCreateSucessfullyWhenNameNotExists(){
        when(categoryRepository.existsByNameIgnoreCaseAndUserId(request.name(), user.getId()))
                .thenReturn(false);
        when(mapper.toCategory(request)).thenReturn(category1);
        when(userService.getLoggedUser()).thenReturn(user);
        when(categoryRepository.save(category1)).thenReturn(category1);
        when(mapper.toCategoryDto(category1)).thenReturn(response);

        CategoryResponseDto actual = categoryService.createCategory(request);
        assertEquals(response, actual);
    }

    @Test
    void shouldReturnCategoryResponseDtoList(){
        when(userService.getLoggedUser()).thenReturn(user);
        when(categoryRepository.findByUserId(user.getId())).thenReturn(list);
        when(mapper.toCategoryDtoList(list)).thenReturn(dtoList);

        List<CategoryResponseDto> actual = categoryService.getCategories();
        assertEquals(dtoList, actual);
    }

    @Test
    void shouldThrowWhenCategoryNotExists(){
        when(userService.getLoggedUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.empty());
        CategoryNotFoundException exception = assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.updateCategory(request, 1L)
        );

        assertEquals("Categoria não encontrada!", exception.getMessage());
    }

    @Test
    void shouldUpdateCategoryWhenExists(){
        CategoryResponseDto expected = CategoryResponseDtoFixture.build(1L, "Salário");
        CategoryRequestDto updateRequest = CategoryRequestDtoFixture.build("Salário");
        when(userService.getLoggedUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.of(category1));
        when(categoryRepository.save(category1)).thenReturn(category1);
        when(mapper.toCategoryDto(category1)).thenReturn(expected);

        CategoryResponseDto actual = categoryService.updateCategory(updateRequest, 1L);
        assertEquals(expected, actual);
    }

    @Test
    void shouldDeleteSucessfully(){
        when(userService.getLoggedUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.of(category1));
        categoryService.deleteCategory(category1.getId());

        verify(categoryRepository).delete(category1);
    }

    @Test
    void shouldThrowWhenDeletingNonExistingCategory(){
        when(userService.getLoggedUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(10L, user.getId())).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(10L)
        );

        assertEquals("Categoria não encontrada!", exception.getMessage());
        verify(categoryRepository, never()).delete(any());;
    }

}
