package com.guuh.transaction_service.api.dto.request;

import com.guuh.transaction_service.api.dto.request.RefreshTokenRequestDto;
import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequestDtoFixture {
    public static RefreshTokenRequestDto build(@NotBlank String refreshToken){
        return new RefreshTokenRequestDto(refreshToken);
    }

}
