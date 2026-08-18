package com.guuh.transaction_service.api.dto.response;

import java.math.BigDecimal;

public record CategoryReportResponseDto(String name, BigDecimal total) {
}
