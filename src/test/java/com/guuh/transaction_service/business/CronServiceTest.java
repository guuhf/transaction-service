package com.guuh.transaction_service.business;

import com.guuh.transaction_service.api.dto.response.ReportResponseDto;
import com.guuh.transaction_service.api.dto.response.ReportResponseDtoFixture;
import com.guuh.transaction_service.infrastructure.client.NotificationClient;
import com.guuh.transaction_service.infrastructure.entity.User;
import com.guuh.transaction_service.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CronServiceTest {
    @InjectMocks
    private CronService cronService;
    @Mock
    private ReportService reportService;
    @Mock
    private NotificationClient client;
    @Mock
    private UserRepository userRepository;
    @Mock
    private User user1;
    @Mock
    private User user2;
    @Mock
    private Page<User> firstPage;
    @Mock
    private Page<User> secondPage;
    @Mock
    private ReportResponseDto report1;
    @Mock
    private ReportResponseDto report2;

    @BeforeEach
    void setup(){
        user1 = User.builder()
                .id(1L)
                .name("Gustavo")
                .email("teste@gmail.com")
                .password("teste123")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Gustavo")
                .email("teste2@gmail.com")
                .password("teste123")
                .build();

        report1 = ReportResponseDtoFixture.build(
                new BigDecimal("5000.00"),
                new BigDecimal("2300.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("3700.00"),
                15,
                LocalDateTime.of(2026, 8, 1, 0, 0),
                LocalDateTime.of(2026, 8, 31, 23, 59),
                List.of()
        );

        report2 = ReportResponseDtoFixture.build(
                new BigDecimal("8200.00"),
                new BigDecimal("3150.00"),
                new BigDecimal("750.00"),
                new BigDecimal("5800.00"),
                22,
                LocalDateTime.of(2026, 7, 1, 0, 0),
                LocalDateTime.of(2026, 7, 31, 23, 59),
                List.of()
        );
    }

    @Test
    void shouldSendEmailSucessfully(){
        when(userRepository.findAll(PageRequest.of(0,100)))
                .thenReturn(firstPage);
        when(userRepository.findAll(PageRequest.of(1,100)))
                .thenReturn(secondPage);
        when(firstPage.iterator()).thenReturn(List.of(user1).iterator());
        when(secondPage.iterator()).thenReturn(List.of(user2).iterator());
        when(firstPage.hasNext()).thenReturn(true);
        when(secondPage.hasNext()).thenReturn(false);
        when(reportService.generateMonthlyReport(1L)).thenReturn(report1);
        when(reportService.generateMonthlyReport(2L)).thenReturn(report2);

        cronService.sendEmail();

        verify(userRepository).findAll(PageRequest.of(0, 100));
        verify(userRepository).findAll(PageRequest.of(1, 100));

        verify(client).sendEmail(report1, "teste@gmail.com");
        verify(client).sendEmail(report2, "teste2@gmail.com");
    }
}
