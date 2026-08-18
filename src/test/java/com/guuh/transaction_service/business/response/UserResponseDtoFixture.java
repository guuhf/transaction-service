package com.guuh.transaction_service.business.response;

import com.guuh.transaction_service.business.dto.response.UserResponseDto;

public class UserResponseDtoFixture {
    public static UserResponseDto build(Long id, String name, String email) {
        return new UserResponseDto(id, name, email);
    }
}
