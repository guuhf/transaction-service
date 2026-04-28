package com.guuh.transaction_service.infrastructure.repository;

import com.guuh.transaction_service.infrastructure.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
