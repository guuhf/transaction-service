package com.guuh.transaction_service.business.dto.response;

import com.guuh.transaction_service.infrastructure.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDto(
        TransactionType transactionType,
        String description,
        BigDecimal amount,
        LocalDateTime date,
        LocalDateTime dueDate,
        Long categoryId,
        String categoryName
) {
}
