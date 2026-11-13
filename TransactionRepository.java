package com.smartbank.repository;

import com.smartbank.model.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findByAccountNumberOrderByCreatedAtDesc(String accountNumber);
    void deleteByAccountNumber(String accountNumber);
}
