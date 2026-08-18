package com.guuh.transaction_service.api.dto.response;

import com.guuh.transaction_service.api.dto.response.CategoryReportResponseDto;

import java.math.BigDecimal;

public class CategoryReportResponseDtoFixture {
    public static CategoryReportResponseDto build(String name,
                                                  BigDecimal total) {
        return new CategoryReportResponseDto(name, total);
    }
}
