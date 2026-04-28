package com.guuh.transaction_service.business;

import com.guuh.transaction_service.business.dto.request.TransactionRequestDto;
import com.guuh.transaction_service.business.dto.response.TransactionResponseDto;
import com.guuh.transaction_service.business.mapper.TransactionMapper;
import com.guuh.transaction_service.infrastructure.entity.Transaction;
import com.guuh.transaction_service.infrastructure.exceptions.InvalidAmountException;
import com.guuh.transaction_service.infrastructure.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper mapper;

    public TransactionResponseDto createTransaction(TransactionRequestDto dto){
        amountValidation(dto.getAmount());
        Transaction transaction = mapper.toTransaction(dto);
        transaction.setDate(LocalDateTime.now());
        return mapper.toTransactionDto(transactionRepository.save(transaction));
    }

    public void amountValidation(BigDecimal amount){
        if (amount.compareTo(BigDecimal.ZERO) >= 0){
            throw new InvalidAmountException("O valor tem que ser maior que zero");
        }
    }
}
