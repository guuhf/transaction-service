package com.guuh.transaction_service.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(@NotBlank String name) {
}
