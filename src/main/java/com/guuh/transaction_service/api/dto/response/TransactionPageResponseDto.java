package com.guuh.transaction_service.api.dto.response;

import java.util.List;

public record TransactionPageResponseDto<T> (
            List<T> content,
            int page,
            int size,
            long totalElements,
            int totalPages
) {}
