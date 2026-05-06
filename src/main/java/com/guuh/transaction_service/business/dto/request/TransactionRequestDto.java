package com.guuh.transaction_service.business.dto.request;

import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDto {
    @NotNull
    private TransactionType transactionType;
    @NotBlank
    private String description;
    @NotNull
    private BigDecimal amount;
    private LocalDateTime dueDate;
    @NotNull
    private Long categoryId;
}
