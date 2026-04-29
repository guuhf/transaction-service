package com.guuh.transaction_service.controller;


import com.guuh.transaction_service.business.CategoryService;
import com.guuh.transaction_service.business.dto.request.CategoryRequestDto;
import com.guuh.transaction_service.business.dto.response.CategoryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions/category")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category management")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Criar uma categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada"),
            @ApiResponse(responseCode = "409", description = "Categoria ja existente!"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody CategoryRequestDto dto){
        return ResponseEntity.status(201).body(categoryService.createCategory(dto));
    }
}
