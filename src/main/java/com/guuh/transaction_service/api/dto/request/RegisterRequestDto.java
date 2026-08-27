package com.guuh.transaction_service.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequestDto(
        @NotBlank(message = "Nome é obrigatório!")
        String name,
        @NotBlank(message = "Email é obrigatório!")
        @Email
        String email,
        @NotBlank(message = "Senha é obrigatório!")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "A senha deve ter pelo menos 8 caracteres, uma letra maiúscula, uma minúscula, um número e um caractere especial"
        )
        String password
) {
}
