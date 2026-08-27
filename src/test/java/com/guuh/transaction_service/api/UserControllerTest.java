package com.guuh.transaction_service.api;

import com.guuh.transaction_service.api.dto.response.UserResponseDto;
import com.guuh.transaction_service.api.dto.response.UserResponseDtoFixture;
import com.guuh.transaction_service.business.UserService;
import com.guuh.transaction_service.infrastructure.exceptions.UserNotFoundException;
import com.guuh.transaction_service.infrastructure.handler.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;

    private UserResponseDto response;
    private MockMvc mockMvc;
    private String url;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new RestExceptionHandler())
                .alwaysDo(print()).build();

        url = "/user";

        response = UserResponseDtoFixture.build(
                1L,
                "Gustavo",
                "teste@gmail.com"
        );
    }

    @Test
    void shouldReturnLoggedUserSucessfully() throws Exception {
        when(userService.getLoggedUserData()).thenReturn(response);

        mockMvc.perform(get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.name").value(response.name()))
                .andExpect(jsonPath("$.email").value(response.email()));

        verify(userService).getLoggedUserData();
    }

    @Test
    void shouldReturnNotFoundWhenThrowException() throws Exception {
        when(userService.getLoggedUserData())
                .thenThrow(new UserNotFoundException("Usuário nãoo encontrado"));

        mockMvc.perform(get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService).getLoggedUserData();
    }
}
