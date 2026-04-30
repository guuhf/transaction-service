package com.guuh.transaction_service.business;

import com.guuh.transaction_service.business.dto.request.CategoryRequestDto;
import com.guuh.transaction_service.business.dto.response.CategoryResponseDto;
import com.guuh.transaction_service.business.mapper.CategoryMapper;
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

    public CategoryResponseDto createCategory(CategoryRequestDto dto){
        validateCategoryUniquess(dto.getName());
        Category category = mapper.toCategory(dto);
        return mapper.toCategoryDto(categoryRepository.save(category));
    }

    public void validateCategoryUniquess(String name){
        if (categoryRepository.existsByName(name)){
            throw new CategoryAlreadyExistsException("Essa categoria ja foi registrada!");
        }
    }

    public List<CategoryResponseDto> getCategories(){
        return mapper.toCategoryDtoList(categoryRepository.findAll());
    }

    public CategoryResponseDto updateCategory(CategoryRequestDto dto, Long id){
        Category category = categoryRepository.findById(id).orElseThrow(()->
                new CategoryNotFoundException("Categoria não encontrada!"));

        category.setName(dto.getName());
        return mapper.toCategoryDto(categoryRepository.save(category));
    }

    public void deleteCategory(Long id){
        categoryRepository.delete(categoryRepository.findById(id).orElseThrow(()->
                new CategoryNotFoundException("Categoria não encontrada!")));
    }
}
