package com.guuh.transaction_service.business;

import com.guuh.transaction_service.business.dto.request.LoginRequestDto;
import com.guuh.transaction_service.business.dto.request.RegisterRequestDto;
import com.guuh.transaction_service.business.dto.response.LoginResponseDto;
import com.guuh.transaction_service.business.dto.response.UserResponseDto;
import com.guuh.transaction_service.business.mapper.UserMapper;
import com.guuh.transaction_service.infrastructure.entity.User;
import com.guuh.transaction_service.infrastructure.exceptions.UserAlreadyExistsException;
import com.guuh.transaction_service.infrastructure.repository.UserRepository;
import com.guuh.transaction_service.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserResponseDto userRegister(RegisterRequestDto dto) {
        validateEmailUniquess(dto.getEmail());
        User user = mapper.toUser(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return mapper.toUserDto(userRepository.save(user));
    }

    public void validateEmailUniquess(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("O usuário ja existe");
        }
    }

    public LoginResponseDto userLogin(LoginRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        return LoginResponseDto.builder()
                .token("Bearer " + jwtUtil.generateToken(authentication.getName()))
                .build();
    }
}
