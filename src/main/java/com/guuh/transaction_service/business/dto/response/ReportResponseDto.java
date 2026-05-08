package com.guuh.transaction_service.business.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponseDto {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private Integer totalTransactions;

    private LocalDateTime initialDate;
    private LocalDateTime finalDate;

    private List<CategoryReportResponseDto> categories;
}
