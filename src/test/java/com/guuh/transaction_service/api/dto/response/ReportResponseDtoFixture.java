package com.guuh.transaction_service.api.dto.response;

import com.guuh.transaction_service.api.dto.response.CategoryReportResponseDto;
import com.guuh.transaction_service.api.dto.response.ReportResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReportResponseDtoFixture {
    public static ReportResponseDto build(BigDecimal totalIncome,
                                          BigDecimal totalExpense,
                                          BigDecimal openingBalance,
                                          BigDecimal balance,
                                          Integer totalTransactions,
                                          LocalDateTime initialDate,
                                          LocalDateTime finalDate,
                                          List<CategoryReportResponseDto> categories) {
        return new ReportResponseDto(totalIncome, totalExpense, openingBalance, balance, totalTransactions, initialDate, finalDate, categories);
    }
}
