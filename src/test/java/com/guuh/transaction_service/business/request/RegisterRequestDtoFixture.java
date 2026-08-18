package com.guuh.transaction_service.business.request;

import com.guuh.transaction_service.business.dto.request.RegisterRequestDto;
import com.guuh.transaction_service.business.dto.request.TransactionRequestDto;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RegisterRequestDtoFixture {
    public static RegisterRequestDto build(@NotBlank
                                           String name,
                                           @NotBlank
                                           @Email
                                           String email,
                                           @NotBlank
                                           @Pattern(
                                                   regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                                                   message = "A senha deve ter pelo menos 8 caracteres, uma letra mai\u00FAscula, uma min\u00FAscula, um n\u00FAmero e um caractere especial"
                                           )
                                           String password) {

        return new RegisterRequestDto(name, email, password);
    }

}
