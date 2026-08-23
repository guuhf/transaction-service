package com.guuh.transaction_service.business;

import com.guuh.transaction_service.api.dto.request.*;
import com.guuh.transaction_service.api.dto.response.LoginResponseDto;
import com.guuh.transaction_service.api.dto.response.LoginResponseDtoFixture;
import com.guuh.transaction_service.api.dto.response.UserResponseDto;
import com.guuh.transaction_service.api.dto.response.UserResponseDtoFixture;
import com.guuh.transaction_service.api.mapper.UserMapper;
import com.guuh.transaction_service.infrastructure.entity.RefreshToken;
import com.guuh.transaction_service.infrastructure.entity.User;
import com.guuh.transaction_service.infrastructure.exceptions.EmailAlreadyExistsException;
import com.guuh.transaction_service.infrastructure.exceptions.UnauthorizedTokenException;
import com.guuh.transaction_service.infrastructure.repository.RefreshTokenRepository;
import com.guuh.transaction_service.infrastructure.repository.UserRepository;
import com.guuh.transaction_service.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserMapper mapper;
    @Mock
    private User user;
    @Mock
    private RefreshToken refreshToken;
    @Mock
    private UserResponseDto userResponse;
    @Mock
    private RegisterRequestDto registerRequest;
    @Mock
    private LoginRequestDto loginRequest;
    @Mock
    private LoginResponseDto loginResponse;
    @Mock
    private RefreshTokenRequestDto refreshTokenRequest;
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup() throws NoSuchAlgorithmException {
        user = User.builder()
                .id(1L)
                .name("Gustavo")
                .email("teste@gmail.com")
                .password("teste123")
                .build();

        refreshToken = RefreshToken.builder()
                .id(1L)
                .token(authService.gerarHashToken("TOKEN"))
                .expirationDate(LocalDate.now().plusDays(15))
                .user(user)
                .build();


        registerRequest = RegisterRequestDtoFixture.build(
                "Gustavo",
                "teste@gmail.com",
                "teste123"
        );

        loginRequest = LoginRequestDtoFixture.build(
                "teste@gmail.com",
                "teste123"
        );

        loginResponse = LoginResponseDtoFixture.build(
                "TOKEN TESTE",
                "REFRESH TOKEN TESTE"
        );

        userResponse = UserResponseDtoFixture.build(
                1L,
                "Gustavo",
                "teste@gmail.com"
        );

        refreshTokenRequest = RefreshTokenRequestDtoFixture.build(
                "Bearer TOKEN"
        );

    }

    @Test
    void shouldThrowWhenRegisteringExistingEmail() {
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(true);
        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.userRegister(registerRequest)
        );

        assertEquals(
                "Email já cadastrado!",
                exception.getMessage()
        );

        verify(userRepository).existsByEmail(registerRequest.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldRegisterSuccessfullyWhenEmailNotExist() {
        when(userRepository.existsByEmail(registerRequest.email())).thenReturn(false);
        when(mapper.toUser(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("PASSWORD-ENCODED");
        when(userRepository.save(user)).thenReturn(user);
        when(mapper.toUserDto(user)).thenReturn(userResponse);

        UserResponseDto actual = authService.userRegister(registerRequest);

        assertEquals(userResponse, actual);
        assertEquals("PASSWORD-ENCODED", user.getPassword());

        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowWhenLoginEmailNotExists() {
        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.userLogin(loginRequest));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldLoginSuccessfullyWhenCredentialsAreCorrect() throws NoSuchAlgorithmException {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(loginRequest.email());
        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findUserByEmail(loginRequest.email())).thenReturn(user);
        mockManageRefreshToken();
        when(jwtUtil.generateToken(user.getEmail())).thenReturn("ACCESS_TOKEN");

        LoginResponseDto actual = authService.userLogin(loginRequest);
        LoginResponseDto expected = LoginResponseDtoFixture.build(
                "Bearer ACCESS_TOKEN",
                "Bearer REFRESH_TOKEN"
        );
        assertEquals(expected, actual);

        verify(refreshTokenRepository)
                .save(any(RefreshToken.class));
    }

    void mockManageRefreshToken() {
        when((refreshTokenRepository.findByUserId(user.getId())))
                .thenReturn(Optional.empty());
        when(jwtUtil.generateRefreshToken(user.getEmail()))
                .thenReturn("REFRESH_TOKEN");
    }

    @Test
    void shouldThrowWhenUserHasNoToken() {
        String token = "TOKEN";
        String email = "teste@gmail.com";
        when(userRepository.findUserByEmail(email)).thenReturn(user);
        when(refreshTokenRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        UnauthorizedTokenException exception = assertThrows(
                UnauthorizedTokenException.class,
                () -> authService.validateRefreshToken(token, email)
        );

        assertEquals("Token inválido!", exception.getMessage());
    }

    @Test
    void shouldThrowWhenTokenIsInvalid() {
        String token = "TOKEN";
        String email = "teste@gmail.com";
        when(userRepository.findUserByEmail(email)).thenReturn(user);
        when(refreshTokenRepository.findByUserId(user.getId())).thenReturn(Optional.of(refreshToken));
        when(jwtUtil.validateToken(token, email)).thenReturn(false);

        UnauthorizedTokenException exception = assertThrows(
                UnauthorizedTokenException.class,
                () -> authService.validateRefreshToken(token, email)
        );

        assertEquals("Token inválido!", exception.getMessage());

    }

    @Test
    void shouldThrowWhenTokenIsNotRefreshToken() {
        String token = "TOKEN";
        String email = "teste@gmail.com";
        when(userRepository.findUserByEmail(email)).thenReturn(user);
        when(refreshTokenRepository.findByUserId(user.getId())).thenReturn(Optional.of(refreshToken));
        when(jwtUtil.validateToken(token, email)).thenReturn(true);
        when(jwtUtil.isRefreshToken(token)).thenReturn(false);

        UnauthorizedTokenException exception = assertThrows(
                UnauthorizedTokenException.class,
                () -> authService.validateRefreshToken(token, email)
        );

        assertEquals("Token inválido!", exception.getMessage());
    }

    @Test
    void shouldThrowWhenRefreshTokenHashDoesNotMatch() throws NoSuchAlgorithmException {
        String token = "TOKEN";
        String email = "teste@gmail.com";
        refreshToken.setToken("TOKEN_DIFERENTE");

        when(userRepository.findUserByEmail(email)).thenReturn(user);
        when(refreshTokenRepository.findByUserId(user.getId())).thenReturn(Optional.of(refreshToken));
        when(jwtUtil.validateToken(token, email)).thenReturn(true);
        when(jwtUtil.isRefreshToken(token)).thenReturn(true);

        UnauthorizedTokenException exception = assertThrows(
                UnauthorizedTokenException.class,
                () -> authService.validateRefreshToken(token, email)
        );

        assertEquals("Token inválido!", exception.getMessage());
    }

    @Test
    void shouldRefreshTokenLoginSucessfully() throws NoSuchAlgorithmException {
        String token = "TOKEN";
        String username = "teste@gmail.com";

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        mockValidateRefreshToken(token, username);
        when(jwtUtil.generateRefreshToken(user.getEmail()))
                .thenReturn("REFRESH_TOKEN");

        when(jwtUtil.generateToken(username)).thenReturn("ACCESS_TOKEN");

        LoginResponseDto actual = authService.refreshTokenLogin(refreshTokenRequest);

        LoginResponseDto expected = LoginResponseDtoFixture.build(
                "Bearer ACCESS_TOKEN",
                "Bearer REFRESH_TOKEN"
        );

        assertEquals(expected, actual);
    }

    void mockValidateRefreshToken(String token, String username) {
        when(userRepository.findUserByEmail(username)).thenReturn(user);
        when(refreshTokenRepository.findByUserId(user.getId())).thenReturn(Optional.of(refreshToken));
        when(jwtUtil.validateToken(token, username)).thenReturn(true);
        when(jwtUtil.isRefreshToken(token)).thenReturn(true);
    }


}
