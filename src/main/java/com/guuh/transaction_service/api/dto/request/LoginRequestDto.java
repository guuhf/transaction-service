package com.guuh.transaction_service.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Email é obrigatório!")
        @Email
        String email,
        @NotBlank(message = "Email é obrigatório!")
        String password
) {
}
