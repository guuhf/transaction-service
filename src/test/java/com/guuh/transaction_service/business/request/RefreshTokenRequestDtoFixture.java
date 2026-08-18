package com.guuh.transaction_service.business.request;

import com.guuh.transaction_service.business.dto.request.CategoryRequestDto;
import com.guuh.transaction_service.business.dto.request.RefreshTokenRequestDto;
import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequestDtoFixture {
    public static RefreshTokenRequestDto build(@NotBlank String refreshToken){
        return new RefreshTokenRequestDto(refreshToken);
    }

}
