package com.guuh.transaction_service.business;

import com.guuh.transaction_service.business.dto.request.FilterRequestDto;
import com.guuh.transaction_service.business.dto.request.TransactionRequestDto;
import com.guuh.transaction_service.business.dto.response.TransactionResponseDto;
import com.guuh.transaction_service.business.mapper.TransactionMapper;
import com.guuh.transaction_service.infrastructure.entity.Category;
import com.guuh.transaction_service.infrastructure.entity.Transaction;
import com.guuh.transaction_service.infrastructure.entity.User;
import com.guuh.transaction_service.infrastructure.exceptions.CategoryNotFoundException;
import com.guuh.transaction_service.infrastructure.exceptions.InvalidAmountException;
import com.guuh.transaction_service.infrastructure.repository.CategoryRepository;
import com.guuh.transaction_service.infrastructure.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper mapper;
    private final AuthService authService;

    public TransactionResponseDto createTransaction(TransactionRequestDto dto) {
        amountValidation(dto.getAmount());
        Transaction transaction = mapper.toTransaction(dto);
        transaction.setDate(LocalDateTime.now());
        transaction.setUser(authService.getLoggedUser());
        Category category = categoryRepository.findByIdAndUserId(dto.getCategoryId(), authService.getLoggedUser().getId()).orElseThrow(() ->
                new CategoryNotFoundException("Categoria não encontrada!"));
        transaction.getCategory().setName(category.getName());
        return mapper.toTransactionDto(transactionRepository.save(transaction));
    }

    public void amountValidation(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("O valor tem que ser maior que zero");
        }
    }

    public List<TransactionResponseDto> findTransactions(FilterRequestDto dto) {
        return mapper.toTransactionDtoList(transactionRepository.findWithFilter(
                authService.getLoggedUser().getId(),
                dto.getCategoryId(),
                dto.getTransactionType(),
                dto.getInitialDate(),
                dto.getFinalDate(),
                dto.getInitialDueDate(),
                dto.getFinalDueDate()
        ));

    }
}
