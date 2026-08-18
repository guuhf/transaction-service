package com.guuh.transaction_service.api;

import com.guuh.transaction_service.business.UserService;
import com.guuh.transaction_service.api.dto.response.UserResponseDto;
import com.guuh.transaction_service.infrastructure.configs.AuthorizationConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "users", description = "User management")
@SecurityRequirement(name = AuthorizationConfig.SECURITY_SCHEME)
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Pegar dados do usuário logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados disponíveis"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    public ResponseEntity<UserResponseDto> getLoggedUser(){
        return ResponseEntity.status(200).body(userService.getLoggedUserData());
    }
}
