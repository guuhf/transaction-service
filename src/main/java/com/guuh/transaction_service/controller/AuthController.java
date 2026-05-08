package com.guuh.transaction_service.controller;


import com.guuh.transaction_service.business.AuthService;
import com.guuh.transaction_service.business.dto.request.RegisterRequestDto;
import com.guuh.transaction_service.business.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User Management")
public class AuthController{

    private final AuthService authService;

    @PostMapping
    @Operation(summary = "Registrar um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado"),
            @ApiResponse(responseCode = "409", description = "Email ja existente"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    public ResponseEntity<UserResponseDto> userRegister(RegisterRequestDto dto){
        return ResponseEntity.status(201).body(authService.userRegister(dto));
    }
}
