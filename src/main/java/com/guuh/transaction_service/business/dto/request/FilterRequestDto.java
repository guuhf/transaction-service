package com.guuh.transaction_service.business.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;

import java.time.LocalDateTime;

public record FilterRequestDto(
        Long categoryId,
        TransactionType transactionType,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime initialDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime finalDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime initialDueDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime finalDueDate
) {
}
