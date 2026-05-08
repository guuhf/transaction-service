package com.guuh.transaction_service.business;

import com.guuh.transaction_service.business.dto.response.CategoryReportResponseDto;
import com.guuh.transaction_service.business.dto.response.ReportResponseDto;
import com.guuh.transaction_service.business.mapper.TransactionMapper;
import com.guuh.transaction_service.infrastructure.entity.Transaction;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import com.guuh.transaction_service.infrastructure.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper mapper;

    public ReportResponseDto generateReport(LocalDateTime initialDate,
                                            LocalDateTime finalDate) {
        List<Transaction> transactions = transactionRepository.findTransactionByDateBetween(initialDate, finalDate);

        BigDecimal totalIncome = calculateTransactionTypeTotal(transactions, TransactionType.INCOME);
        BigDecimal totalExpense = calculateTransactionTypeTotal(transactions, TransactionType.EXPENSE);
        return ReportResponseDto.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(totalIncome.subtract(totalExpense))
                .totalTransactions(transactions.size())
                .initialDate(initialDate)
                .finalDate(finalDate)
                .categories(calculateCategoryTotals(transactions))
                .build();
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

    public List<CategoryReportResponseDto> generateCategoryReport(Map<String, BigDecimal> categoryReport){

        List<CategoryReportResponseDto> categories = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : categoryReport.entrySet()){
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