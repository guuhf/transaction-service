package com.guuh.transaction_service.business;

import com.guuh.transaction_service.api.dto.request.FilterRequestDto;
import com.guuh.transaction_service.api.dto.request.TransactionRequestDto;
import com.guuh.transaction_service.api.dto.response.TransactionPageResponseDto;
import com.guuh.transaction_service.api.dto.response.TransactionResponseDto;
import com.guuh.transaction_service.api.mapper.TransactionMapper;
import com.guuh.transaction_service.infrastructure.entity.Category;
import com.guuh.transaction_service.infrastructure.entity.Transaction;
import com.guuh.transaction_service.infrastructure.enums.TransactionStatus;
import com.guuh.transaction_service.infrastructure.exceptions.CategoryNotFoundException;
import com.guuh.transaction_service.infrastructure.exceptions.InvalidAmountException;
import com.guuh.transaction_service.infrastructure.exceptions.TransactionIsAlreadyCanceledException;
import com.guuh.transaction_service.infrastructure.exceptions.TransactionNotFoundException;
import com.guuh.transaction_service.infrastructure.repository.CategoryRepository;
import com.guuh.transaction_service.infrastructure.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper mapper;
    private final UserService userService;

    public TransactionResponseDto createTransaction(TransactionRequestDto dto) {
        amountValidation(dto.amount());
        Transaction transaction = mapper.toTransaction(dto);
        transaction.setTransactionStatus(TransactionStatus.COMPLETED);
        transaction.setDate(LocalDateTime.now());
        transaction.setUser(userService.getLoggedUser());
        Category category = categoryRepository.findByIdAndUserId(dto.categoryId(), userService.getLoggedUser().getId()).orElseThrow(() ->
                new CategoryNotFoundException("Categoria não encontrada!"));
        transaction.setCategory(category);
        return mapper.toTransactionDto(transactionRepository.save(transaction));
    }

    public TransactionResponseDto cancelTransaction(Long id){
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userService.getLoggedUser().getId())
                .orElseThrow(()-> new TransactionNotFoundException("Transação não encontrada!"));
        checkStatus(transaction.getTransactionStatus());
        transaction.setTransactionStatus(TransactionStatus.CANCELED);
        return mapper.toTransactionDto(transactionRepository.save(transaction));
    }

    public void checkStatus(TransactionStatus status){
        if (status.equals(TransactionStatus.CANCELED)){
            throw new TransactionIsAlreadyCanceledException("Essa transação já foi cancelada.");
        }
    }

    public void amountValidation(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("O valor tem que ser maior que zero");
        }
    }

    public TransactionPageResponseDto<TransactionResponseDto> findTransactions(FilterRequestDto dto, int page) {
       return mapper.toResponseDtoPage(transactionRepository.findWithFilter(
                userService.getLoggedUser().getId(),
                dto.categoryId(),
                dto.transactionType(),
                dto.initialDate(),
                dto.finalDate(),
                dto.initialDueDate(),
                dto.finalDueDate(),
                PageRequest.of(page, 15)
        ));

    }

}
