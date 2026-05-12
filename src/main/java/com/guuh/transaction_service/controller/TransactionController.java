package com.guuh.transaction_service.controller;

import com.guuh.transaction_service.business.TransactionService;
import com.guuh.transaction_service.business.dto.request.FilterRequestDto;
import com.guuh.transaction_service.business.dto.request.TransactionRequestDto;
import com.guuh.transaction_service.business.dto.response.TransactionResponseDto;
import com.guuh.transaction_service.infrastructure.configs.AuthorizationConfig;
import com.guuh.transaction_service.infrastructure.security.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction management")
@SecurityRequirement(name = AuthorizationConfig.SECURITY_SCHEME)
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Criar uma transação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação criada"),
            @ApiResponse(responseCode = "400", description = "O valor da transação é inválido"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    private ResponseEntity<TransactionResponseDto> createTransaction(@RequestBody TransactionRequestDto dto){
        return ResponseEntity.status(201).body(transactionService.createTransaction(dto));
    }

    @GetMapping
    @Operation(summary = "Listar as transações")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transações listadas"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    private ResponseEntity<List<TransactionResponseDto>> getTransactions(FilterRequestDto dto) {
        return ResponseEntity.status(200).body(transactionService.findTransactions(dto));
    }

}
