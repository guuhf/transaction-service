package com.guuh.transaction_service.business.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.guuh.transaction_service.business.dto.request.FilterRequestDto;
import com.guuh.transaction_service.business.dto.request.RefreshTokenRequestDto;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class FilterRequestDtoFixture {
    public static FilterRequestDto build(Long categoryId,
                                         TransactionType transactionType,
                                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
                                         LocalDateTime initialDate,
                                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
                                         LocalDateTime finalDate,
                                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
                                         LocalDateTime initialDueDate,
                                         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
                                         LocalDateTime finalDueDate) {
        return new FilterRequestDto(categoryId, transactionType, initialDate, finalDate, initialDueDate, finalDueDate);
    }

}
