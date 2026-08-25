package com.guuh.transaction_service.business;

import com.guuh.transaction_service.api.dto.response.UserResponseDto;
import com.guuh.transaction_service.api.dto.response.UserResponseDtoFixture;
import com.guuh.transaction_service.api.mapper.UserMapper;
import com.guuh.transaction_service.infrastructure.entity.User;
import com.guuh.transaction_service.infrastructure.exceptions.UserNotFoundException;
import com.guuh.transaction_service.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper mapper;
    @Mock
    private User user;
    @Mock
    private UserResponseDto response;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .name("Gustavo")
                .email("teste@gmail.com")
                .password("teste123")
                .build();

        response = UserResponseDtoFixture.build(
                1L,
                "Gustavo",
                "teste@gmail.com"
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnLoggedUserSuccessfully() {
        mockAuthentication(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User actual = userService.getLoggedUser();

        assertEquals(user, actual);
        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void shouldThrowWhenLoggedUserNotFound() {
        mockAuthentication(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                userService::getLoggedUser
        );

        assertEquals("Usuário não encontrado", exception.getMessage());
    }

    @Test
    void shouldReturnLoggedUserDataSuccessfully() {
        mockAuthentication(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(mapper.toUserDto(user)).thenReturn(response);

        UserResponseDto actual = userService.getLoggedUserData();

        assertEquals(response, actual);
        verify(mapper).toUserDto(user);
    }

    private void mockAuthentication(String email) {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(email, null));
    }
}
