package com.guuh.transaction_service.api;

import com.guuh.transaction_service.api.dto.request.*;
import com.guuh.transaction_service.api.dto.response.LoginResponseDto;
import com.guuh.transaction_service.api.dto.response.LoginResponseDtoFixture;
import com.guuh.transaction_service.api.dto.response.UserResponseDto;
import com.guuh.transaction_service.api.dto.response.UserResponseDtoFixture;
import com.guuh.transaction_service.business.AuthService;
import com.guuh.transaction_service.infrastructure.exceptions.EmailAlreadyExistsException;
import com.guuh.transaction_service.infrastructure.exceptions.UnauthorizedTokenException;
import com.guuh.transaction_service.infrastructure.handler.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @InjectMocks
    private AuthController authController;
    @Mock

    private AuthService authService;
    private RegisterRequestDto registerRequest;
    private UserResponseDto userResponse;
    private LoginRequestDto loginRequest;
    private LoginResponseDto loginResponse;
    private RefreshTokenRequestDto refreshTokenRequest;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private String registerUrl;
    private String loginUrl;
    private String refreshTokenUrl;
    private String registerRequestJson;
    private String loginRequestJson;
    private String refreshTokenRequestJson;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new RestExceptionHandler())
                .alwaysDo(print()).build();

        registerUrl = "/auth";
        loginUrl = "/auth/login";
        refreshTokenUrl = "/auth/refresh-token";

        registerRequest = RegisterRequestDtoFixture.build(
                "Gustavo",
                "teste@gmail.com",
                "Teste123!"
        );
        userResponse = UserResponseDtoFixture.build(
                1L,
                "Gustavo",
                "teste@gmail.com"
        );
        loginRequest = LoginRequestDtoFixture.build(
                "teste@gmail.com",
                "Teste123!"
        );

        loginResponse = LoginResponseDtoFixture.build(
                "TOKEN TESTE",
                "REFRESH TOKEN TESTE"
        );

        refreshTokenRequest = RefreshTokenRequestDtoFixture.build(
                "Bearer TOKEN"
        );

        registerRequestJson = objectMapper.writeValueAsString(registerRequest);
        loginRequestJson = objectMapper.writeValueAsString(loginRequest);
        refreshTokenRequestJson = objectMapper.writeValueAsString(refreshTokenRequest);


    }

    @Test
    void shouldRegisterUserSucessfully() throws Exception {
        when(authService.userRegister(registerRequest)).thenReturn(userResponse);

        mockMvc.perform(post(registerUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(registerRequestJson)
                ).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.name").value(userResponse.name()))
                .andExpect(jsonPath("$.email").value(userResponse.email()));

        verify(authService).userRegister(registerRequest);
    }

    @Test
    void shouldReturnBadRequestStatusWhenRegister() throws Exception {
        RegisterRequestDto invalidRequest = RegisterRequestDtoFixture.build(
                "",
                "email-invalido",
                ""
        );

        mockMvc.perform(post(registerUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        ).andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldRegisterReturnConflictWhenThrowException() throws Exception {
        when(authService.userRegister(registerRequest))
                .thenThrow(new EmailAlreadyExistsException("Email já cadastrado!"));

        mockMvc.perform(post(registerUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(registerRequestJson)
        ).andExpect(status().isConflict());

        verify(authService).userRegister(registerRequest);
    }

    @Test
    void shouldLoginUserSucessfully() throws Exception {
        when(authService.userLogin(loginRequest)).thenReturn(loginResponse);

        mockMvc.perform(post(loginUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(loginResponse.token()))
                .andExpect(jsonPath("$.refreshToken").value(loginResponse.refreshToken()));

        verify(authService).userLogin(loginRequest);
    }

    @Test
    void shouldReturnBadRequestStatusWhenLogin() throws Exception {
        LoginRequestDto invalidRequest = LoginRequestDtoFixture.build(
                "email-invalido",
                ""
        );

        mockMvc.perform(post(loginUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        ).andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldLoginReturnUnauthorizedWhenThrowException() throws Exception {
        when(authService.userLogin(loginRequest))
                .thenThrow(new BadCredentialsException("Email ou senha inválidos!"));

        mockMvc.perform(post(loginUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(loginRequestJson)
        ).andExpect(status().isUnauthorized());

        verify(authService).userLogin(loginRequest);

    }

    @Test
    void shouldRefreshTokenLoginSucessfully() throws Exception {
        when(authService.refreshTokenLogin(refreshTokenRequest)).thenReturn(loginResponse);

        mockMvc.perform(post(refreshTokenUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(refreshTokenRequestJson)
                ).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(loginResponse.token()))
                .andExpect(jsonPath("$.refreshToken").value(loginResponse.refreshToken()));

        verify(authService).refreshTokenLogin(refreshTokenRequest);
    }

    @Test
    void shouldReturnBadRequestWhenRefreshTokenLogin() throws Exception {
        RefreshTokenRequestDto invalidRequest = RefreshTokenRequestDtoFixture.build(
                ""
        );

        mockMvc.perform(post(refreshTokenUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        ).andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void shouldRefreshTokenLoginReturnUnauthorizedWhenThrowException() throws Exception {
        when(authService.refreshTokenLogin(refreshTokenRequest))
                .thenThrow(new UnauthorizedTokenException("Token inválido!"));

        mockMvc.perform(post(refreshTokenUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(refreshTokenRequestJson)
        ).andExpect(status().isUnauthorized());

        verify(authService).refreshTokenLogin(refreshTokenRequest);

    }
}
