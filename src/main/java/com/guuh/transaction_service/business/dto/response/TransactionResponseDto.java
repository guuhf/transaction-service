package com.guuh.transaction_service.business.dto.response;

import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDto {
    private TransactionType transactionType;
    private String description;
    private BigDecimal amount;
    private LocalDateTime date;
    private LocalDateTime dueDate;
    private Long categoryId;
    private String categoryName;
}
