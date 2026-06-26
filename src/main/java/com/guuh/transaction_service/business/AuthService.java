package com.guuh.transaction_service.business;

import com.guuh.transaction_service.business.dto.request.LoginRequestDto;
import com.guuh.transaction_service.business.dto.request.RefreshTokenRequestDto;
import com.guuh.transaction_service.business.dto.request.RegisterRequestDto;
import com.guuh.transaction_service.business.dto.response.LoginResponseDto;
import com.guuh.transaction_service.business.dto.response.UserResponseDto;
import com.guuh.transaction_service.business.mapper.UserMapper;
import com.guuh.transaction_service.infrastructure.entity.RefreshToken;
import com.guuh.transaction_service.infrastructure.entity.User;
import com.guuh.transaction_service.infrastructure.exceptions.UnauthorizedTokenException;
import com.guuh.transaction_service.infrastructure.exceptions.UserAlreadyExistsException;
import com.guuh.transaction_service.infrastructure.repository.RefreshTokenRepository;
import com.guuh.transaction_service.infrastructure.repository.UserRepository;
import com.guuh.transaction_service.infrastructure.security.JwtUtil;
import com.guuh.transaction_service.infrastructure.security.UserDetailsServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private TextEncryptor textEncryptor;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Value("${TOKEN_ENCRYPT_KEY}")
    private String secretKey;

    @Value("${TOKEN_ENCRYPT_SALT}")
    private String salt;


    @PostConstruct
    public void initEncryptor() {
        this.textEncryptor = Encryptors.delux(secretKey, salt);
    }


    public UserResponseDto userRegister(RegisterRequestDto dto) {
        validateEmailUniquess(dto.getEmail());
        User user = mapper.toUser(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return mapper.toUserDto(userRepository.save(user));
    }

    public void validateEmailUniquess(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BadCredentialsException("Email ou senha inválidos!");
        }
    }

    @Transactional
    public LoginResponseDto userLogin(LoginRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        User user = userRepository.findUserByEmail(dto.getEmail());
        RefreshToken refreshToken = manageRefreshToken(user);

        return LoginResponseDto.builder()
                .token("Bearer " + jwtUtil.generateToken(authentication.getName()))
                .refreshToken("Bearer " + textEncryptor.decrypt(refreshToken.getToken()))
                .build();
    }

    @Transactional
    public RefreshToken manageRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    RefreshToken token = new RefreshToken();
                    token.setUser(user);
                    return token;
                });
        this.textEncryptor = Encryptors.delux(secretKey, salt);

        refreshToken.setToken(textEncryptor.encrypt(jwtUtil.generateRefreshToken(user.getEmail())));
        refreshToken.setExpirationDate(LocalDate.now().plusDays(15));

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public LoginResponseDto refreshTokenLogin(RefreshTokenRequestDto dto) {
        String username = jwtUtil.extractUsername(dto.getRefreshToken());
        if (!jwtUtil.validateToken(dto.getRefreshToken(), username) || !jwtUtil.isRefreshToken(dto.getRefreshToken())) {
            throw new UnauthorizedTokenException("Refresh token inválido");
        }
        validateRefreshToken(dto.getRefreshToken(), username);

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
        User user = userRepository.findUserByEmail(userDetails.getUsername());
        RefreshToken refreshToken = manageRefreshToken(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return LoginResponseDto.builder()
                .token(jwtUtil.generateToken(userDetails.getUsername()))
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public void validateRefreshToken(String token, String email) {
        User user = userRepository.findUserByEmail(email);
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId()).orElseThrow(() ->
                new UnauthorizedTokenException("Token inválido!"));
        String tokenDecrypted = textEncryptor.decrypt(refreshToken.getToken());
        if (!token.equals(tokenDecrypted)){
            throw new UnauthorizedTokenException("Token inválido");
        }
        if (refreshToken.getExpirationDate().isBefore(LocalDate.now()) || !refreshToken.getUser().getId().equals(user.getId())){
            throw new UnauthorizedTokenException("Token inválido");
        }
    }


    public boolean isSigned() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public boolean isNotSigned() {
        return !isSigned();
    }
}


