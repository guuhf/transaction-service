package com.guuh.transaction_service.business.response;

import com.guuh.transaction_service.business.dto.response.CategoryReportResponseDto;
import com.guuh.transaction_service.business.dto.response.ReportResponseDto;
import com.guuh.transaction_service.business.dto.response.TransactionResponseDto;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionResponseDtoFixture {
    public static TransactionResponseDto build(TransactionType transactionType,
                                               String description,
                                               BigDecimal amount,
                                               LocalDateTime date,
                                               LocalDateTime dueDate,
                                               Long categoryId,
                                               String categoryName) {
        return new TransactionResponseDto(transactionType, description, amount, date, dueDate, categoryId, categoryName);
    }
}
