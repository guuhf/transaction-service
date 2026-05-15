package com.guuh.transaction_service.business;

import com.guuh.transaction_service.business.dto.response.UserResponseDto;
import com.guuh.transaction_service.business.mapper.UserMapper;
import com.guuh.transaction_service.infrastructure.entity.User;
import com.guuh.transaction_service.infrastructure.exceptions.UserNotFoundException;
import com.guuh.transaction_service.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    public User getLoggedUser  (){
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email).orElseThrow(()->
                new UserNotFoundException("Usuário não encontrado"));
    }

    public UserResponseDto getLoggedUserDate(){
        return mapper.toUserDto(getLoggedUser());
    }
}

