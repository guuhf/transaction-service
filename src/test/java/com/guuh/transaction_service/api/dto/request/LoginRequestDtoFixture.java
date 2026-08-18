package com.guuh.transaction_service.api.dto.request;

import com.guuh.transaction_service.api.dto.request.LoginRequestDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDtoFixture {
    public static LoginRequestDto build(@NotBlank
                                              @Email
                                              String email,
                                              @NotBlank
                                              String password) {

        return new LoginRequestDto(email, password);
    }

}
