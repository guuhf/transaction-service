package com.guuh.transaction_service.business.response;

import com.guuh.transaction_service.business.dto.response.CategoryResponseDto;
import com.guuh.transaction_service.business.dto.response.LoginResponseDto;

public class LoginResponseDtoFixture {
    public static LoginResponseDto build(String token,
                                         String refreshToken) {
        return new LoginResponseDto(token, refreshToken);
    }
}
