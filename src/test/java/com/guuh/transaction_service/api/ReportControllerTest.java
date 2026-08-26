package com.guuh.transaction_service.api;

import com.guuh.transaction_service.api.dto.response.CategoryReportResponseDtoFixture;
import com.guuh.transaction_service.api.dto.response.ReportResponseDto;
import com.guuh.transaction_service.api.dto.response.ReportResponseDtoFixture;
import com.guuh.transaction_service.business.ReportService;
import com.guuh.transaction_service.infrastructure.exceptions.InvalidDatesException;
import com.guuh.transaction_service.infrastructure.handler.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {
    @InjectMocks
    private ReportController reportController;
    @Mock
    private ReportService reportService;

    private ReportResponseDto response;
    private MockMvc mockMvc;
    private String url;
    private LocalDateTime initialDate;
    private LocalDateTime finalDate;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(reportController)
                .setControllerAdvice(new RestExceptionHandler())
                .alwaysDo(print()).build();

        url = "/transactions/report";
        initialDate = LocalDateTime.of(2026, 8, 1, 0, 0);
        finalDate = LocalDateTime.of(2026, 8, 26, 23, 59);

        response = ReportResponseDtoFixture.build(
                new BigDecimal("5000.00"),
                new BigDecimal("950.00"),
                BigDecimal.ZERO,
                new BigDecimal("4050.00"),
                3,
                initialDate,
                finalDate,
                List.of(
                        CategoryReportResponseDtoFixture.build("Salario", new BigDecimal("5000.00")),
                        CategoryReportResponseDtoFixture.build("Alimentacao", new BigDecimal("650.00"))
                )
        );
    }

    @Test
    void shouldGenerateReportSucessfully() throws Exception {
        when(reportService.generateReport(initialDate, finalDate)).thenReturn(response);

        mockMvc.perform(get(url)
                        .param("initialDate", initialDate.toString())
                        .param("finalDate", finalDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalIncome").value(response.totalIncome().doubleValue()))
                .andExpect(jsonPath("$.totalExpense").value(response.totalExpense().doubleValue()))
                .andExpect(jsonPath("$.openingBalance").value(response.openingBalance().doubleValue()))
                .andExpect(jsonPath("$.balance").value(response.balance().doubleValue()))
                .andExpect(jsonPath("$.totalTransactions").value(response.totalTransactions()))
                .andExpect(jsonPath("$.categories[0].name").value(response.categories().get(0).name()))
                .andExpect(jsonPath("$.categories[0].total").value(response.categories().get(0).total().doubleValue()));

        verify(reportService).generateReport(initialDate, finalDate);
    }

    @Test
    void shouldReturnBadRequestWhenGenerateReportWithoutDates() throws Exception {
        mockMvc.perform(get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(reportService);
    }

    @Test
    void shouldGenerateReportReturnConflictWhenThrowException() throws Exception {
        when(reportService.generateReport(initialDate, finalDate))
                .thenThrow(new InvalidDatesException("O intervalo entre datas nao pode ser maior que 90 dias"));

        mockMvc.perform(get(url)
                        .param("initialDate", initialDate.toString())
                        .param("finalDate", finalDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(reportService).generateReport(initialDate, finalDate);
    }
}
