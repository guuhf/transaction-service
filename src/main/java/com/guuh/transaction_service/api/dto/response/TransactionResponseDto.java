package com.guuh.transaction_service.api.dto.response;

import com.guuh.transaction_service.infrastructure.enums.TransactionStatus;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDto(
        TransactionType transactionType,
        TransactionStatus transactionStatus,
        String description,
        BigDecimal amount,
        LocalDateTime date,
        LocalDateTime dueDate,
        Long categoryId,
        String categoryName
) {
}
