package com.guuh.transaction_service.api.dto.response;

import com.guuh.transaction_service.api.dto.response.UserResponseDto;

public class UserResponseDtoFixture {
    public static UserResponseDto build(Long id, String name, String email) {
        return new UserResponseDto(id, name, email);
    }
}
