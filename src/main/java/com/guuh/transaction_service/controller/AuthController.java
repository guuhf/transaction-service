package com.guuh.transaction_service.controller;


import com.guuh.transaction_service.business.AuthService;
import com.guuh.transaction_service.business.dto.request.LoginRequestDto;
import com.guuh.transaction_service.business.dto.request.RegisterRequestDto;
import com.guuh.transaction_service.business.dto.response.LoginResponseDto;
import com.guuh.transaction_service.business.dto.response.UserResponseDto;
import com.guuh.transaction_service.infrastructure.configs.AuthorizationConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth Management")
@SecurityRequirement(name = AuthorizationConfig.SECURITY_SCHEME)
public class AuthController{

    private final AuthService authService;

    @PostMapping
    @Operation(summary = "Registrar um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado"),
            @ApiResponse(responseCode = "409", description = "Email ja existente"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    public ResponseEntity<UserResponseDto> userRegister(@RequestBody @Valid RegisterRequestDto dto){
        return ResponseEntity.status(201).body(authService.userRegister(dto));
    }

    @PostMapping("/login")
    @Operation(summary = "Login de usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário logado"),
            @ApiResponse(responseCode = "401", description = "Credenciais Inválidas"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    public ResponseEntity<LoginResponseDto> userLogin(@RequestBody @Valid LoginRequestDto dto){
        return ResponseEntity.status(200).body(authService.userLogin(dto));
    }
}
