package com.guuh.transaction_service.api.mapper;

import com.guuh.transaction_service.api.dto.request.RegisterRequestDto;
import com.guuh.transaction_service.api.dto.response.UserResponseDto;
import com.guuh.transaction_service.api.mapper.UserMapper;
import com.guuh.transaction_service.infrastructure.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    UserMapper userMapper;
    User user;
    UserResponseDto response;
    RegisterRequestDto request;

    @BeforeEach
    public void setup(){
        userMapper = Mappers.getMapper(UserMapper.class);
        user = User.builder()
                .id(1L)
                .name("Gustavo")
                .email("gustavo.jacintof@gmail.com")
                .password("gu080408")
                .build();

        request = new RegisterRequestDto(
                "Gustavo",
                "gustavo.jacintof@gmail.com",
                "gu080408"
        );

        response = new UserResponseDto(
                1L,
                "Gustavo",
                "gustavo.jacintof@gmail.com"
        );
    }

    @Test
    void shouldMapRegisterRequestToUser(){
        User actual = userMapper.toUser(request);
        assertAll(
                () -> assertEquals(request.name(), actual.getName()),
                () -> assertEquals(request.email(), actual.getEmail()),
                () -> assertEquals(request.password(), actual.getPassword())
        );
    }

    @Test
    void shouldMapUserToUserResponse(){
        UserResponseDto actual = userMapper.toUserDto(user);
        assertEquals(response, actual);
    }
}
