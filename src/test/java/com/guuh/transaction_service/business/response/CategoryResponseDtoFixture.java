package com.guuh.transaction_service.business.response;

import com.guuh.transaction_service.business.dto.response.CategoryResponseDto;

import java.math.BigDecimal;

public class CategoryResponseDtoFixture{
    public static CategoryResponseDto build(Long id,
                                            String name) {
        return new CategoryResponseDto(id, name);
    }
}
