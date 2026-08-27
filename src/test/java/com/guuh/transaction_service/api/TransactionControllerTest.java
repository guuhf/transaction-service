package com.guuh.transaction_service.api;

import com.guuh.transaction_service.api.dto.request.FilterRequestDto;
import com.guuh.transaction_service.api.dto.request.FilterRequestDtoFixture;
import com.guuh.transaction_service.api.dto.request.TransactionRequestDto;
import com.guuh.transaction_service.api.dto.request.TransactionRequestDtoFixture;
import com.guuh.transaction_service.api.dto.response.TransactionPageResponseDto;
import com.guuh.transaction_service.api.dto.response.TransactionResponseDto;
import com.guuh.transaction_service.api.dto.response.TransactionResponseDtoFixture;
import com.guuh.transaction_service.business.TransactionService;
import com.guuh.transaction_service.infrastructure.enums.TransactionStatus;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import com.guuh.transaction_service.infrastructure.exceptions.CategoryNotFoundException;
import com.guuh.transaction_service.infrastructure.exceptions.InvalidAmountException;
import com.guuh.transaction_service.infrastructure.exceptions.TransactionIsAlreadyCanceledException;
import com.guuh.transaction_service.infrastructure.handler.RestExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {
    @InjectMocks
    private TransactionController transactionController;
    @Mock
    private TransactionService transactionService;

    private TransactionRequestDto request;
    private TransactionResponseDto response;
    private FilterRequestDto filterRequest;
    private TransactionPageResponseDto<TransactionResponseDto>responsePage;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private String url;
    private String requestJson;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(transactionController)
                .setControllerAdvice(new RestExceptionHandler())
                .alwaysDo(print()).build();

        LocalDateTime date = LocalDateTime.of(2026, 8, 26, 10, 30);
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 30, 10, 30);

        url = "/transactions";

        request = TransactionRequestDtoFixture.build(
                TransactionType.INCOME,
                "Pagamento",
                new java.math.BigDecimal("2500.00"),
                dueDate,
                1L
        );

        response = TransactionResponseDtoFixture.build(
                TransactionType.INCOME,
                TransactionStatus.COMPLETED,
                "Pagamento",
                new java.math.BigDecimal("2500.00"),
                date,
                dueDate,
                1L,
                "Salario"
        );

        filterRequest = FilterRequestDtoFixture.build(
                1L,
                TransactionType.INCOME,
                date.minusDays(5),
                date.plusDays(5),
                dueDate.minusDays(5),
                dueDate.plusDays(5)
        );

        responsePage = new TransactionPageResponseDto<TransactionResponseDto>(
                List.of(response),
                0,
                15,
                1,
                1
        );

        requestJson = objectMapper.writeValueAsString(request);
    }

    @Test
    void shouldCreateTransactionSucessfully() throws Exception {
        when(transactionService.createTransaction(request)).thenReturn(response);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactionType").value(response.transactionType().toString()))
                .andExpect(jsonPath("$.description").value(response.description()))
                .andExpect(jsonPath("$.amount").value(response.amount().doubleValue()))
                .andExpect(jsonPath("$.categoryId").value(response.categoryId()))
                .andExpect(jsonPath("$.categoryName").value(response.categoryName()));

        verify(transactionService).createTransaction(request);
    }

    @Test
    void shouldReturnBadRequestStatusWhenCreateTransaction() throws Exception {
        TransactionRequestDto invalidRequest = TransactionRequestDtoFixture.build(
                null,
                "",
                null,
                null,
                null
        );

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transactionService);
    }

    @Test
    void shouldCreateTransactionReturnBadRequestWhenThrowException() throws Exception {
        when(transactionService.createTransaction(request))
                .thenThrow(new InvalidAmountException("O valor tem que ser maior que zero"));

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(transactionService).createTransaction(request);
    }

    @Test
    void shouldCreateTransactionReturnNotFoundWhenThrowException() throws Exception {
        when(transactionService.createTransaction(request))
                .thenThrow(new CategoryNotFoundException("Categoria não encontrada!"));

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());

        verify(transactionService).createTransaction(request);
    }

    @Test
    void shouldReturnTransactionsSucessfully() throws Exception {
        when(transactionService.findTransactions(filterRequest, 0)).thenReturn(responsePage);

        mockMvc.perform(get(url)
                        .param("page", "0")
                        .param("categoryId", String.valueOf(filterRequest.categoryId()))
                        .param("transactionType", filterRequest.transactionType().toString())
                        .param("initialDate", filterRequest.initialDate().toString())
                        .param("finalDate", filterRequest.finalDate().toString())
                        .param("initialDueDate", filterRequest.initialDueDate().toString())
                        .param("finalDueDate", filterRequest.finalDueDate().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].transactionType").value(response.transactionType().toString()))
                .andExpect(jsonPath("$.content[0].description").value(response.description()))
                .andExpect(jsonPath("$.content[0].amount").value(response.amount().doubleValue()))
                .andExpect(jsonPath("$.content[0].categoryId").value(response.categoryId()))
                .andExpect(jsonPath("$.content[0].categoryName").value(response.categoryName()))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(transactionService).findTransactions(filterRequest, 0);
    }

    @Test
    void shouldReturnBadRequestWhenGetTransactionsWithoutPage() throws Exception {
        mockMvc.perform(get(url)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transactionService);
    }

    @Test
    void shouldCancelTransactionSucessfully() throws Exception {
        when(transactionService.cancelTransaction(1L)).thenReturn(response);

        mockMvc.perform(patch("/transactions/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionType").value(response.transactionType().toString()))
                .andExpect(jsonPath("$.transactionStatus").value(response.transactionStatus().toString()))
                .andExpect(jsonPath("$.description").value(response.description()))
                .andExpect(jsonPath("$.amount").value(response.amount().doubleValue()))
                .andExpect(jsonPath("$.categoryId").value(response.categoryId()))
                .andExpect(jsonPath("$.categoryName").value(response.categoryName()));

        verify(transactionService).cancelTransaction(1L);
    }

    @Test
    void shouldReturnBadRequest() throws Exception {
        when(transactionService.cancelTransaction(1L))
                .thenThrow(new TransactionIsAlreadyCanceledException("Essa transação já foi cancelada."));

        mockMvc.perform(patch("/transactions/1/cancel"))
                .andExpect(status().isBadRequest());

        verify(transactionService).cancelTransaction(1L);
    }

}
