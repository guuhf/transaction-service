package com.guuh.transaction_service.controller;


import com.guuh.transaction_service.business.CategoryService;
import com.guuh.transaction_service.business.dto.request.CategoryRequestDto;
import com.guuh.transaction_service.business.dto.response.CategoryResponseDto;
import com.guuh.transaction_service.infrastructure.configs.AuthorizationConfig;
import com.guuh.transaction_service.infrastructure.security.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions/category")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category management")
@SecurityRequirement(name = AuthorizationConfig.SECURITY_SCHEME)
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Criar uma categoria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada"),
            @ApiResponse(responseCode = "409", description = "Categoria ja existente!"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody @Valid CategoryRequestDto dto){
        return ResponseEntity.status(201).body(categoryService.createCategory(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todas as categorias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefas listadas"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    public ResponseEntity<List<CategoryResponseDto>> getCategories(){
        return ResponseEntity.status(200).body(categoryService.getCategories());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mudar o nome das categorias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada!"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    public ResponseEntity<CategoryResponseDto> updateCategory(@RequestBody @Valid CategoryRequestDto dto,
                                                              @PathVariable Long id){
        return ResponseEntity.status(200).body(categoryService.updateCategory(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar categorias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria deletada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.status(204).build();
    }
}
