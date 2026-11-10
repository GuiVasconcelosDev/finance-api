package com.guivasconcelos.finance_api.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.guivasconcelos.finance_api.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID>{

     @EntityGraph(attributePaths = {"account", "category"})   
     Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);
    
     @EntityGraph(attributePaths = {"account", "category"})
    List<Transaction> findAllByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            UUID userId, LocalDate start, LocalDate end);

        boolean existsByAccountId(UUID accountId);

        boolean existsByCategoryId(UUID categoryId);

}
