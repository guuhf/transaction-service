package com.guuh.transaction_service.api.dto.request;

import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequestDto(
        @NotNull(message = "Tipo de transação é obrigatório!")
        TransactionType transactionType,
        @NotBlank(message = "Descrição é obrigatório!")
        String description,
        @NotNull(message = "Valor é obrigatório!")
        BigDecimal amount,
        LocalDateTime dueDate,
        @NotNull(message = "Categoria é obrigatória")
        Long categoryId
) {
}
