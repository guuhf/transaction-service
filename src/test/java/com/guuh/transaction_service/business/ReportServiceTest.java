package com.guuh.transaction_service.business;

import com.guuh.transaction_service.api.dto.response.CategoryReportResponseDtoFixture;
import com.guuh.transaction_service.api.dto.response.ReportResponseDto;
import com.guuh.transaction_service.api.dto.response.ReportResponseDtoFixture;
import com.guuh.transaction_service.infrastructure.client.NotificationClient;
import com.guuh.transaction_service.infrastructure.entity.Category;
import com.guuh.transaction_service.infrastructure.entity.Transaction;
import com.guuh.transaction_service.infrastructure.entity.User;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import com.guuh.transaction_service.infrastructure.exceptions.InvalidDatesException;
import com.guuh.transaction_service.infrastructure.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    @InjectMocks
    private ReportService reportService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserService userService;
    @Mock
    private Transaction transaction1;
    @Mock
    private Transaction transaction2;
    @Mock
    private Transaction transaction3;
    @Mock
    private Category category1;
    @Mock
    private Category category2;
    @Mock
    private Category category3;
    @Mock
    private List<Transaction> list;
    @Mock
    private ReportResponseDto response;
    @Mock
    private User user;
    @Mock
    private NotificationClient notificationClient;
    private LocalDateTime initialDate;
    private LocalDateTime finalDate;

    @BeforeEach
    void setup() {
        initialDate = LocalDateTime.of(2026, 8, 1, 0, 0);
        finalDate = LocalDateTime.of(2026, 8, 25, 23, 59);

        user = User.builder()
                .id(1L)
                .name("Gustavo")
                .email("teste@gmail.com")
                .password("teste123")
                .build();

        category1 = Category.builder()
                .id(1L)
                .name("Salário")
                .transactions(List.of())
                .user(user)
                .build();

        category2 = Category.builder()
                .id(2L)
                .name("Alimentação")
                .transactions(List.of())
                .user(user)
                .build();

        category3 = Category.builder()
                .id(3L)
                .name("Transporte")
                .transactions(List.of())
                .user(user)
                .build();

        transaction1 = Transaction.builder()
                .id(1L)
                .transactionType(TransactionType.INCOME)
                .description("Salário")
                .amount(new BigDecimal("5000.00"))
                .date(LocalDateTime.of(2026, 8, 5, 10, 0))
                .dueDate(LocalDateTime.of(2026, 8, 5, 10, 0))
                .category(category1)
                .user(user)
                .build();

        transaction2 = Transaction.builder()
                .id(2L)
                .transactionType(TransactionType.EXPENSE)
                .description("Alimentação")
                .amount(new BigDecimal("650.00"))
                .date(LocalDateTime.of(2026, 8, 10, 14, 30))
                .dueDate(LocalDateTime.of(2026, 8, 10, 14, 30))
                .category(category2)
                .user(user)
                .build();

        transaction3 = Transaction.builder()
                .id(3L)
                .transactionType(TransactionType.EXPENSE)
                .description("Transporte")
                .amount(new BigDecimal("300.00"))
                .date(LocalDateTime.of(2026, 8, 15, 18, 0))
                .dueDate(LocalDateTime.of(2026, 8, 15, 18, 0))
                .category(category3)
                .user(user)
                .build();

        list = List.of(
                transaction1,
                transaction2,
                transaction3
        );

        response = ReportResponseDtoFixture.build(
                new BigDecimal("5000.00"),
                new BigDecimal("950.00"),
                BigDecimal.ZERO,
                new BigDecimal("4050.00"),
                3,
                initialDate,
                finalDate,
                List.of(
                        CategoryReportResponseDtoFixture.build(
                                "Salário",
                                new BigDecimal("5000.00")
                        ),
                        CategoryReportResponseDtoFixture.build(
                                "Alimentação",
                                new BigDecimal("650.00")
                        ),
                        CategoryReportResponseDtoFixture.build(
                                "Transporte",
                                new BigDecimal("300.00")
                        )
                )
        );
    }


    @Test
    void shouldGenerateReportSucessfully() {
        when(userService.getLoggedUser()).thenReturn(user);
        reportService.checkDate(initialDate, finalDate);
        when(transactionRepository.findByUserIdAndDateBetween(user.getId(), initialDate, finalDate))
                .thenReturn(list);

        ReportResponseDto actual = reportService.generateReport(initialDate, finalDate);
        assertEquals(response, actual);
    }

    @Test
    void shouldGenerateMonthlyReportSuccessfully() {
        when(transactionRepository.findByUserIdAndDateBetween(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(list);

        ReportResponseDto actual = reportService.generateMonthlyReport(1L);

        assertEquals(new BigDecimal("5000.00"), actual.totalIncome());
        assertEquals(new BigDecimal("950.00"), actual.totalExpense());
        assertEquals(BigDecimal.ZERO, actual.openingBalance());
        assertEquals(new BigDecimal("4050.00"), actual.balance());
        assertEquals(3, actual.totalTransactions());

        assertEquals(actual.finalDate().minusMonths(1), actual.initialDate());

        verify(transactionRepository).findByUserIdAndDateBetween(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void shouldThrowWhenDatePeriodIsHigherThan90(){
        LocalDateTime initialDate = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime finalDate = LocalDateTime.of(2026, 5, 1, 0, 0);
        assertThrows(
                InvalidDatesException.class,
                () -> reportService.checkDate(initialDate, finalDate)
        );
    }
}
