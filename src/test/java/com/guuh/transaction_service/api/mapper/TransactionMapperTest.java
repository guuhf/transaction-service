package com.guuh.transaction_service.api.mapper;

import com.guuh.transaction_service.api.dto.request.TransactionRequestDto;
import com.guuh.transaction_service.api.dto.response.TransactionPageResponseDto;
import com.guuh.transaction_service.api.dto.response.TransactionResponseDto;
import com.guuh.transaction_service.infrastructure.entity.Category;
import com.guuh.transaction_service.infrastructure.entity.Transaction;
import com.guuh.transaction_service.infrastructure.entity.User;
import com.guuh.transaction_service.infrastructure.enums.TransactionStatus;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionMapperTest {
    User user;
    Category category;
    Transaction transaction1;
    Transaction transaction2;
    Pageable pageable;
    Page<Transaction> transactionPage;
    TransactionRequestDto request;
    TransactionResponseDto response;
    List<Transaction> list;
    List<TransactionResponseDto> dtoList;
    TransactionMapper mapper;
    LocalDateTime date = LocalDateTime.of(2026, 8, 17, 15, 48);
    LocalDateTime dueDate = LocalDateTime.of(2026, 9, 17, 15, 48);

    @BeforeEach
    public void setup() {
        mapper = Mappers.getMapper(TransactionMapper.class);

        pageable = PageRequest.of(0, 10);

        user = User.builder()
                .id(1L)
                .name("Gustavo")
                .email("teste@gmail.com")
                .password("teste123")
                .build();

        category = Category.builder()
                .id(1L)
                .name("Salário")
                .build();

        transaction1 = Transaction.builder()
                .id(1L)
                .transactionType(TransactionType.INCOME)
                .transactionStatus(TransactionStatus.COMPLETED)
                .description("teste")
                .amount(new BigDecimal(100))
                .date(date)
                .dueDate(dueDate)
                .category(category)
                .build();

        transaction2 = Transaction.builder()
                .id(2L)
                .transactionType(TransactionType.EXPENSE)
                .transactionStatus(TransactionStatus.COMPLETED)
                .description("teste")
                .amount(new BigDecimal(100))
                .date(date)
                .dueDate(dueDate)
                .category(category)
                .build();

        response = new TransactionResponseDto(
                1L,
                TransactionType.INCOME,
                TransactionStatus.COMPLETED,
                "teste",
                new BigDecimal(100),
                date,
                dueDate,
                1L,
                "Salário"
        );

        request = new TransactionRequestDto(
                TransactionType.INCOME,
                "teste",
                new BigDecimal(100),
                dueDate,
                1L
                );

        list = List.of(transaction1, transaction2);
        dtoList = List.of(
                new TransactionResponseDto(
                        1L,
                        TransactionType.INCOME,
                        TransactionStatus.COMPLETED,
                        "teste",
                        new BigDecimal(100),
                        date,
                        dueDate,
                        1L,
                        "Salário"
                ),

                new TransactionResponseDto(
                        2L,
                        TransactionType.EXPENSE,
                        TransactionStatus.COMPLETED,
                        "teste",
                        new BigDecimal(100),
                        date,
                        dueDate,
                        1L,
                        "Salário"
                )
        );

        transactionPage = new PageImpl<>(
                list,
                pageable,
                list.size()
        );
    }

    @Test
    void shouldMapTransactionToTransactionResponse() {
        TransactionResponseDto actual = mapper.toTransactionDto(transaction1);
        assertEquals(response, actual);
    }

    @Test
    void shouldMapTransactionRequestToTransaction() {
        Transaction actual = mapper.toTransaction(request);
        assertAll(
                () -> assertEquals(request.transactionType(), actual.getTransactionType()),
                () -> assertEquals(request.description(), actual.getDescription()),
                () -> assertEquals(request.amount(), actual.getAmount()),
                () -> assertEquals(request.dueDate(), actual.getDueDate()),
                () -> assertEquals(request.categoryId(), actual.getCategory().getId())
        );
    }

    @Test
    void shouldMapTransactionListToDtoList(){
        List<TransactionResponseDto> actual = mapper.toTransactionDtoList(list);
        assertEquals(dtoList, actual);
    }

    @Test
    void shouldMapTransactionPageToResponseDtoPage(){
        TransactionPageResponseDto<TransactionResponseDto> actual = mapper.toResponseDtoPage(transactionPage);
        assertAll(
                () -> assertEquals(dtoList, actual.content()),
                () -> assertEquals(0, actual.page()),
                () -> assertEquals(2, actual.totalElements()),
                () -> assertEquals(0, actual.page()),
                () -> assertEquals(10, actual.size())
        );
    }
}
