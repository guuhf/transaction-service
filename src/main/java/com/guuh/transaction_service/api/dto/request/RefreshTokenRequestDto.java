package com.guuh.transaction_service.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(
        @NotBlank(message = "Refresh Token é obrigatório!")
        String refreshToken
) {
}
