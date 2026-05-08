package com.guuh.transaction_service.business.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryReportResponseDto {
    private String name;
    private BigDecimal total;
}
