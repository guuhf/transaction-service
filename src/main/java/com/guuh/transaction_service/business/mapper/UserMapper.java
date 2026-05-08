package com.guuh.transaction_service.business.mapper;


import com.guuh.transaction_service.business.dto.request.RegisterRequestDto;
import com.guuh.transaction_service.business.dto.response.UserResponseDto;
import com.guuh.transaction_service.infrastructure.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.lang.annotation.Target;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    User toUser (RegisterRequestDto dto);

    UserResponseDto toUserDto(User user);
}
