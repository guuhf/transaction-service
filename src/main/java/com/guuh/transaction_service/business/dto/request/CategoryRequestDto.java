package com.guuh.transaction_service.business.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(@NotBlank String name) {
}
