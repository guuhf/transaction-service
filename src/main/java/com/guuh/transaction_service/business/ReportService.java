package com.guuh.transaction_service.business;

import com.guuh.transaction_service.api.dto.response.CategoryReportResponseDto;
import com.guuh.transaction_service.api.dto.response.ReportResponseDto;
import com.guuh.transaction_service.infrastructure.client.NotificationClient;
import com.guuh.transaction_service.infrastructure.entity.Transaction;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import com.guuh.transaction_service.infrastructure.exceptions.InvalidDatesException;
import com.guuh.transaction_service.infrastructure.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final NotificationClient notificationClient;

    public ReportResponseDto generateReport(LocalDateTime initialDate,
                                            LocalDateTime finalDate) {
        Long userId = userService.getLoggedUser().getId();

        ReportResponseDto report = buildReport(userId, initialDate, finalDate);
        notificationClient.sendEmail(report, userService.getLoggedUser().getEmail());

        return report;
    }

    public ReportResponseDto generateMonthlyReport(Long userId) {
        LocalDateTime finalDate = LocalDateTime.now();
        LocalDateTime initialDate = finalDate.minusMonths(1);

        return buildReport(userId, initialDate, finalDate);
    }

    private ReportResponseDto buildReport(Long userId,
                                          LocalDateTime initialDate,
                                          LocalDateTime finalDate) {
        checkDate(initialDate, finalDate);

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(userId, initialDate, finalDate);

        BigDecimal totalIncome = calculateTransactionTypeTotal(transactions, TransactionType.INCOME);
        BigDecimal totalExpense = calculateTransactionTypeTotal(transactions, TransactionType.EXPENSE);
        BigDecimal openingBalance = calculateTransactionTypeTotal(transactions, TransactionType.OPENINGBALANCE);

        return new ReportResponseDto(
                totalIncome,
                totalExpense,
                openingBalance,
                totalIncome.add(openingBalance).subtract(totalExpense),
                transactions.size(),
                initialDate,
                finalDate,
                calculateCategoryTotals(transactions)
        );
    }

    public void checkDate(LocalDateTime initialDate,
                          LocalDateTime finalDate) {
        long period = ChronoUnit.DAYS.between(initialDate, finalDate);
        if (period > 90 || initialDate.isAfter(finalDate)) {
            throw new InvalidDatesException("Periodo de datas inválidos.");
        }
    }

    public BigDecimal calculateTransactionTypeTotal(List<Transaction> transactions,
                                                    TransactionType transactionType) {
        BigDecimal total = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionType() == transactionType) {
                total = total.add(transaction.getAmount());
            }
        }
        return total;
    }

    public List<CategoryReportResponseDto> calculateCategoryTotals(List<Transaction> transactions) {

        Map<String, BigDecimal> categoryReport = new LinkedHashMap<>();

        for (Transaction transaction : transactions) {

            if (transaction.getTransactionType() == TransactionType.OPENINGBALANCE) {
                continue;
            }

            String categoryName =
                    transaction.getCategory().getName();

            BigDecimal currentTotal =
                    categoryReport.getOrDefault(categoryName, BigDecimal.ZERO);

            categoryReport.put(
                    categoryName,
                    currentTotal.add(transaction.getAmount())
            );
        }

        return generateCategoryReport(categoryReport);
    }

    public List<CategoryReportResponseDto> generateCategoryReport(Map<String, BigDecimal> categoryReport) {

        List<CategoryReportResponseDto> categories = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : categoryReport.entrySet()) {
            categories.add(
                    new CategoryReportResponseDto(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return categories;
    }
}
