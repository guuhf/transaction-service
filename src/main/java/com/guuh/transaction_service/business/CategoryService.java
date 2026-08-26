package com.guuh.transaction_service.business;

import com.guuh.transaction_service.api.dto.request.CategoryRequestDto;
import com.guuh.transaction_service.api.dto.response.CategoryResponseDto;
import com.guuh.transaction_service.api.mapper.CategoryMapper;
import com.guuh.transaction_service.infrastructure.entity.Category;
import com.guuh.transaction_service.infrastructure.exceptions.CategoryAlreadyExistsException;
import com.guuh.transaction_service.infrastructure.exceptions.CategoryNotFoundException;
import com.guuh.transaction_service.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;
    private final UserService userService;

    public CategoryResponseDto createCategory(CategoryRequestDto dto){
        validateCategoryUniqueness(dto.name());
        Category category = mapper.toCategory(dto);
        category.setUser(userService.getLoggedUser());
        return mapper.toCategoryDto(categoryRepository.save(category));
    }

    public void validateCategoryUniqueness(String name){
        if (categoryRepository.existsByNameIgnoreCaseAndUserId(name, userService.getLoggedUser().getId())){
            throw new CategoryAlreadyExistsException("Essa categoria ja foi registrada!");
        }
    }

    public List<CategoryResponseDto> getCategories(){
        return mapper.toCategoryDtoList(categoryRepository.findByUserId(userService.getLoggedUser().getId()));
    }

    public CategoryResponseDto updateCategory(CategoryRequestDto dto, Long id){
        validateCategoryUniqueness(dto.name());
        Category category = categoryRepository.findByIdAndUserId(
                id, userService.getLoggedUser().getId()).orElseThrow(()->
                new CategoryNotFoundException("Categoria não encontrada!"));

        category.setName(dto.name());
        return mapper.toCategoryDto(categoryRepository.save(category));
    }

    public void deleteCategory(Long id){
        categoryRepository.delete(categoryRepository.findByIdAndUserId(
                id, userService.getLoggedUser().getId()).orElseThrow(()->
                new CategoryNotFoundException("Categoria não encontrada!")));
    }
}
