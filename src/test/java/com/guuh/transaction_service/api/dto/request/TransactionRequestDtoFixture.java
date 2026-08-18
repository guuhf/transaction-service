package com.guuh.transaction_service.api.dto.request;

import com.guuh.transaction_service.api.dto.request.TransactionRequestDto;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionRequestDtoFixture {
    public static TransactionRequestDto build(@NotNull
                                              TransactionType transactionType,
                                              @NotBlank
                                              String description,
                                              @NotNull
                                              BigDecimal amount,
                                              LocalDateTime dueDate,
                                              @NotNull
                                              Long categoryId) {

        return new TransactionRequestDto(transactionType, description, amount, dueDate, categoryId);
    }

}
