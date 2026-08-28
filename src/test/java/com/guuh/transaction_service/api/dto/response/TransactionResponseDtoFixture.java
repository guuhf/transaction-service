package com.guuh.transaction_service.api.dto.response;

import com.guuh.transaction_service.api.dto.response.TransactionResponseDto;
import com.guuh.transaction_service.infrastructure.enums.TransactionStatus;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDtoFixture {
    public static TransactionResponseDto build(Long id,
                                               TransactionType transactionType,
                                               TransactionStatus transactionStatus,
                                               String description,
                                               BigDecimal amount,
                                               LocalDateTime date,
                                               LocalDateTime dueDate,
                                               Long categoryId,
                                               String categoryName) {
        return new TransactionResponseDto(id, transactionType, transactionStatus, description, amount, date, dueDate, categoryId, categoryName);
    }
}
