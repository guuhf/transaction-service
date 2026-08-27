package com.guuh.transaction_service.api.dto.response;

import com.guuh.transaction_service.api.dto.response.CategoryResponseDto;

public class CategoryResponseDtoFixture{
    public static CategoryResponseDto build(Long id,
                                            String name) {
        return new CategoryResponseDto(id, name);
    }
}
