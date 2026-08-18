package com.guuh.transaction_service.api.dto.response;

import com.guuh.transaction_service.api.dto.response.LoginResponseDto;

public class LoginResponseDtoFixture {
    public static LoginResponseDto build(String token,
                                         String refreshToken) {
        return new LoginResponseDto(token, refreshToken);
    }
}
