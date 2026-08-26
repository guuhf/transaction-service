package com.guuh.transaction_service.api;

import com.guuh.transaction_service.api.dto.request.CategoryRequestDto;
import com.guuh.transaction_service.api.dto.request.CategoryRequestDtoFixture;
import com.guuh.transaction_service.api.dto.response.CategoryResponseDto;
import com.guuh.transaction_service.api.dto.response.CategoryResponseDtoFixture;
import com.guuh.transaction_service.business.CategoryService;
import com.guuh.transaction_service.infrastructure.exceptions.CategoryAlreadyExistsException;
import com.guuh.transaction_service.infrastructure.exceptions.CategoryNotFoundException;
import com.guuh.transaction_service.infrastructure.handler.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {
    @InjectMocks
    private CategoryController categoryController;
    @Mock
    private CategoryService categoryService;

    private CategoryRequestDto request;
    private CategoryRequestDto updateRequest;
    private CategoryResponseDto response;
    private CategoryResponseDto updatedResponse;
    private List<CategoryResponseDto> responseList;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private String url;
    private String requestJson;
    private String updateRequestJson;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .setControllerAdvice(new RestExceptionHandler())
                .alwaysDo(print()).build();

        url = "/transactions/category";

        request = CategoryRequestDtoFixture.build("Seguro");
        updateRequest = CategoryRequestDtoFixture.build("Salario");

        response = CategoryResponseDtoFixture.build(
                1L,
                "Seguro"
        );

        updatedResponse = CategoryResponseDtoFixture.build(
                1L,
                "Salario"
        );

        responseList = List.of(
                response,
                CategoryResponseDtoFixture.build(2L, "Alimentacao")
        );

        requestJson = objectMapper.writeValueAsString(request);
        updateRequestJson = objectMapper.writeValueAsString(updateRequest);
    }

    @Test
    void shouldCreateCategorySucessfully() throws Exception {
        when(categoryService.createCategory(request)).thenReturn(response);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.name").value(response.name()));

        verify(categoryService).createCategory(request);
    }

    @Test
    void shouldReturnBadRequestStatusWhenCreateCategory() throws Exception {
        CategoryRequestDto invalidRequest = CategoryRequestDtoFixture.build("");

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService);
    }

    @Test
    void shouldCreateCategoryReturnConflictWhenThrowException() throws Exception {
        when(categoryService.createCategory(request))
                .thenThrow(new CategoryAlreadyExistsException("Essa categoria ja foi registrada!"));

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict());

        verify(categoryService).createCategory(request);
    }

    @Test
    void shouldReturnCategoryListSucessfully() throws Exception {
        when(categoryService.getCategories()).thenReturn(responseList);

        mockMvc.perform(get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(responseList.get(0).id()))
                .andExpect(jsonPath("$[0].name").value(responseList.get(0).name()))
                .andExpect(jsonPath("$[1].id").value(responseList.get(1).id()))
                .andExpect(jsonPath("$[1].name").value(responseList.get(1).name()));

        verify(categoryService).getCategories();
    }

    @Test
    void shouldUpdateCategorySucessfully() throws Exception {
        when(categoryService.updateCategory(updateRequest, 1L)).thenReturn(updatedResponse);

        mockMvc.perform(put(url + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(updatedResponse.id()))
                .andExpect(jsonPath("$.name").value(updatedResponse.name()));

        verify(categoryService).updateCategory(updateRequest, 1L);
    }

    @Test
    void shouldReturnBadRequestStatusWhenUpdateCategory() throws Exception {
        CategoryRequestDto invalidRequest = CategoryRequestDtoFixture.build("");

        mockMvc.perform(put(url + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoryService);
    }

    @Test
    void shouldUpdateCategoryReturnNotFoundWhenThrowException() throws Exception {
        when(categoryService.updateCategory(updateRequest, 1L))
                .thenThrow(new CategoryNotFoundException("Categoria não encontrada!"));

        mockMvc.perform(put(url + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson))
                .andExpect(status().isNotFound());

        verify(categoryService).updateCategory(updateRequest, 1L);
    }

    @Test
    void shouldDeleteCategorySucessfully() throws Exception {
        mockMvc.perform(delete(url + "/1"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    void shouldDeleteCategoryReturnNotFoundWhenThrowException() throws Exception {
        doThrow(new CategoryNotFoundException("Categoria não encontrada!"))
                .when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete(url + "/1"))
                .andExpect(status().isNotFound());

        verify(categoryService).deleteCategory(1L);
    }
}
