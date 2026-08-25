package com.guuh.transaction_service.business;

import com.guuh.transaction_service.api.dto.request.FilterRequestDto;
import com.guuh.transaction_service.api.dto.request.FilterRequestDtoFixture;
import com.guuh.transaction_service.api.dto.request.TransactionRequestDto;
import com.guuh.transaction_service.api.dto.request.TransactionRequestDtoFixture;
import com.guuh.transaction_service.api.dto.response.TransactionResponseDto;
import com.guuh.transaction_service.api.dto.response.TransactionResponseDtoFixture;
import com.guuh.transaction_service.api.mapper.TransactionMapper;
import com.guuh.transaction_service.infrastructure.entity.Category;
import com.guuh.transaction_service.infrastructure.entity.Transaction;
import com.guuh.transaction_service.infrastructure.entity.User;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import com.guuh.transaction_service.infrastructure.exceptions.CategoryNotFoundException;
import com.guuh.transaction_service.infrastructure.exceptions.InvalidAmountException;
import com.guuh.transaction_service.infrastructure.repository.CategoryRepository;
import com.guuh.transaction_service.infrastructure.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @InjectMocks
    private TransactionService transactionService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TransactionMapper mapper;
    @Mock
    private UserService userService;
    @Mock
    private User user;
    @Mock
    private Category category;
    @Mock
    private Transaction transaction;
    @Mock
    private TransactionRequestDto request;
    @Mock
    private TransactionResponseDto response;
    @Mock
    private FilterRequestDto filterRequest;
    @Mock
    private Page<Transaction> transactionPage;
    @Mock
    private Page<TransactionResponseDto> responsePage;

    @BeforeEach
    void setup() {
        LocalDateTime date = LocalDateTime.of(2026, 8, 25, 10, 30);
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 30, 10, 30);

        user = User.builder()
                .id(1L)
                .name("Gustavo")
                .email("teste@gmail.com")
                .password("teste123")
                .build();

        category = Category.builder()
                .id(1L)
                .name("Salario")
                .user(user)
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .transactionType(TransactionType.INCOME)
                .description("Pagamento")
                .amount(new BigDecimal("2500.00"))
                .date(date)
                .dueDate(dueDate)
                .category(Category.builder().id(1L).build())
                .build();

        request = TransactionRequestDtoFixture.build(
                TransactionType.INCOME,
                "Pagamento",
                new BigDecimal("2500.00"),
                dueDate,
                1L
        );

        response = TransactionResponseDtoFixture.build(
                TransactionType.INCOME,
                "Pagamento",
                new BigDecimal("2500.00"),
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

        transactionPage = new PageImpl<>(
                List.of(transaction),
                PageRequest.of(0, 15),
                1
        );

        responsePage = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, 15),
                1
        );
    }

    @Test
    void shouldCreateTransactionSuccessfully() {
        when(mapper.toTransaction(request)).thenReturn(transaction);
        when(userService.getLoggedUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(request.categoryId(), user.getId())).thenReturn(Optional.of(category));
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(mapper.toTransactionDto(transaction)).thenReturn(response);

        TransactionResponseDto actual = transactionService.createTransaction(request);

        assertEquals(response, actual);
    }

    @Test
    void shouldThrowWhenAmountIsZeroOrLower() {
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> transactionService.amountValidation(BigDecimal.ZERO)
        );

        assertEquals("O valor tem que ser maior que zero", exception.getMessage());
    }

    @Test
    void shouldThrowWhenCategoryNotExists() {
        when(mapper.toTransaction(request)).thenReturn(transaction);
        when(userService.getLoggedUser()).thenReturn(user);
        when(categoryRepository.findByIdAndUserId(request.categoryId(), user.getId())).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(
                CategoryNotFoundException.class,
                () -> transactionService.createTransaction(request)
        );

        assertEquals("Categoria não encontrada!", exception.getMessage());
    }

    @Test
    void shouldReturnFilteredTransactionsPage() {
        when(userService.getLoggedUser()).thenReturn(user);
        when(transactionRepository.findWithFilter(
                user.getId(),
                filterRequest.categoryId(),
                filterRequest.transactionType(),
                filterRequest.initialDate(),
                filterRequest.finalDate(),
                filterRequest.initialDueDate(),
                filterRequest.finalDueDate(),
                PageRequest.of(0, 15)
        )).thenReturn(transactionPage);
        when(mapper.toResponseDtoPage(transactionPage)).thenReturn(responsePage);

        Page<TransactionResponseDto> actual = transactionService.findTransactions(filterRequest, 0);

        assertEquals(responsePage, actual);
        verify(transactionRepository).findWithFilter(
                user.getId(),
                filterRequest.categoryId(),
                filterRequest.transactionType(),
                filterRequest.initialDate(),
                filterRequest.finalDate(),
                filterRequest.initialDueDate(),
                filterRequest.finalDueDate(),
                PageRequest.of(0, 15)
        );
        verify(mapper).toResponseDtoPage(transactionPage);
    }
}
