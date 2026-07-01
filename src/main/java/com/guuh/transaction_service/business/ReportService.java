package com.guuh.transaction_service.business;

import com.guuh.transaction_service.business.dto.response.CategoryReportResponseDto;
import com.guuh.transaction_service.business.dto.response.ReportResponseDto;
import com.guuh.transaction_service.infrastructure.entity.Transaction;
import com.guuh.transaction_service.infrastructure.entity.User;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import com.guuh.transaction_service.infrastructure.exceptions.DateLimitException;
import com.guuh.transaction_service.infrastructure.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public ReportResponseDto generateReport(LocalDateTime initialDate,
                                            LocalDateTime finalDate) {
        checkDate(initialDate, finalDate);
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
                userService.getLoggedUser().getId(), initialDate,
                finalDate
        );

        BigDecimal totalIncome = calculateTransactionTypeTotal(transactions, TransactionType.INCOME);
        BigDecimal totalExpense = calculateTransactionTypeTotal(transactions, TransactionType.EXPENSE);
        BigDecimal openingBalance = calculateTransactionTypeTotal(transactions, TransactionType.OPENINGBALANCE);
        return ReportResponseDto.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .openingBalance(openingBalance)
                .balance(totalIncome.add(openingBalance).subtract(totalExpense))
                .totalTransactions(transactions.size())
                .initialDate(initialDate)
                .finalDate(finalDate)
                .categories(calculateCategoryTotals(transactions))
                .build();
    }

    public void checkDate(LocalDateTime initialDate,
                          LocalDateTime finalDate) {
       long period = ChronoUnit.DAYS.between(initialDate, finalDate);
        if(period > 90){
            throw new DateLimitException("O periodo de datas ultrapassa o limite de 90 dias.");
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

        Map<String, BigDecimal> categoryReport = new HashMap<>();

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