package com.guuh.transaction_service.api;

import com.guuh.transaction_service.business.ReportService;
import com.guuh.transaction_service.api.dto.response.ReportResponseDto;
import com.guuh.transaction_service.infrastructure.configs.AuthorizationConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/transactions/report")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Report Management")
@SecurityRequirement(name = AuthorizationConfig.SECURITY_SCHEME)
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @Operation(summary = "Fazer um relatório")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório criado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado")
    })
    public ResponseEntity<ReportResponseDto> generateReport(@RequestParam("initialDate")LocalDateTime initialDate,
                                          @RequestParam("finalDate")LocalDateTime finalDate){
        return ResponseEntity.status(200).body(reportService.generateReport(initialDate,finalDate));
    }
}
