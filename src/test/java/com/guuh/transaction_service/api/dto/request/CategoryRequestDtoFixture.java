package com.guuh.transaction_service.api.dto.request;

import com.guuh.transaction_service.api.dto.request.CategoryRequestDto;
import jakarta.validation.constraints.NotBlank;

public class CategoryRequestDtoFixture {
    public static CategoryRequestDto build(@NotBlank String name){
        return new CategoryRequestDto(name);
    }

}
