package com.guuh.transaction_service.business.request;

import com.guuh.transaction_service.business.dto.request.CategoryRequestDto;
import jakarta.validation.constraints.NotBlank;

public class CategoryRequestDtoFixture {
    public static CategoryRequestDto build(@NotBlank String name){
        return new CategoryRequestDto(name);
    }

}
