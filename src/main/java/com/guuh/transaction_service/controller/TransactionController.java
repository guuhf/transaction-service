package com.guuh.transaction_service.controller;

import com.guuh.transaction_service.business.TransactionService;
import com.guuh.transaction_service.business.dto.request.TransactionRequestDto;
import com.guuh.transaction_service.business.dto.response.TransactionResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction management")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @Operation(summary = "Criar uma transação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação criada"),
            @ApiResponse(responseCode = "409", description = "O valor da transação é inválido"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    private ResponseEntity<TransactionResponseDto> createTransaction(@RequestBody TransactionRequestDto dto){
        return ResponseEntity.status(201).body(transactionService.createTransaction(dto));
    }

}
