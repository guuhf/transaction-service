package com.guuh.transaction_service.business.request;

import com.guuh.transaction_service.business.dto.request.LoginRequestDto;
import com.guuh.transaction_service.business.dto.request.TransactionRequestDto;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoginRequestDtoFixture {
    public static LoginRequestDto build(@NotBlank
                                              @Email
                                              String email,
                                              @NotBlank
                                              String password) {

        return new LoginRequestDto(email, password);
    }

}
