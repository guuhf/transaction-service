package com.guuh.transaction_service.infrastructure.repository;

import com.guuh.transaction_service.infrastructure.entity.Transaction;
import com.guuh.transaction_service.infrastructure.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
    SELECT t FROM Transaction t
    WHERE (:categoryId IS NULL OR t.category.id = :categoryId)
    AND (:transactionType IS NULL OR t.transactionType = :transactionType)
    AND (CAST(:initialDate AS timestamp) IS NULL OR t.date >= :initialDate)
    AND (CAST(:finalDate AS timestamp) IS NULL OR t.date <= :finalDate)
    AND (CAST(:initialDueDate AS timestamp) IS NULL OR t.dueDate >= :initialDueDate)
    AND (CAST(:finalDueDate AS timestamp) IS NULL OR t.dueDate <= :finalDueDate)
""")
    List<Transaction> findWithFilter(
            @Param("categoryId")Long categoryId,
            @Param("transactionType")TransactionType transactionType,
            @Param("initialDate")LocalDateTime initialDate,
            @Param("finalDate")LocalDateTime finalDate,
            @Param("initialDueDate")LocalDateTime initialDueDate,
            @Param("finalDueDate")LocalDateTime finalDueDate
    );

}
