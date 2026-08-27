package com.guuh.transaction_service.infrastructure.repository;

import com.guuh.transaction_service.infrastructure.entity.Transaction;
import com.guuh.transaction_service.infrastructure.enums.TransactionStatus;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
                SELECT t FROM Transaction t
                WHERE t.user.id = :userId
                AND (:categoryId IS NULL OR t.category.id = :categoryId)
                AND (:transactionType IS NULL OR t.transactionType = :transactionType)
                AND (CAST(:initialDate AS LocalDateTime) IS NULL OR t.date >= :initialDate)
                AND (CAST(:finalDate AS LocalDateTime) IS NULL OR t.date <= :finalDate)
                AND (CAST(:initialDueDate AS LocalDateTime) IS NULL OR t.dueDate >= :initialDueDate)
                AND (CAST(:finalDueDate AS LocalDateTime) IS NULL OR t.dueDate <= :finalDueDate)
            """)
    Page<Transaction> findWithFilter(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("transactionType") TransactionType transactionType,
            @Param("initialDate") LocalDateTime initialDate,
            @Param("finalDate") LocalDateTime finalDate,
            @Param("initialDueDate") LocalDateTime initialDueDate,
            @Param("finalDueDate") LocalDateTime finalDueDate,
            Pageable pageable
    );

    List<Transaction> findByUserIdAndDateBetweenAndTransactionStatus(Long userId,
                                                                     LocalDateTime initialDate,
                                                                     LocalDateTime finalDate,
                                                                     TransactionStatus status);

    Optional<Transaction> findTransactionById(Long id);
}
