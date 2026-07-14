package com.guuh.transaction_service.business.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReportResponseDto(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal openingBalance,
        BigDecimal balance,
        Integer totalTransactions,
        LocalDateTime initialDate,
        LocalDateTime finalDate,
        List<CategoryReportResponseDto> categories
) {
}
