package com.guivasconcelos.finance_api.repository;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.guivasconcelos.finance_api.model.Transaction;
import com.guivasconcelos.finance_api.model.enums.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, UUID>{

     @EntityGraph(attributePaths = {"account", "category"})   
     Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);
    
     @EntityGraph(attributePaths = {"account", "category"})
    List<Transaction> findAllByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            UUID userId, LocalDate start, LocalDate end);

        boolean existsByAccountId(UUID accountId);

        boolean existsByCategoryId(UUID categoryId);

        interface TypeTotal {
            TransactionType getEntryType();
            BigDecimal getTotal();
        }

        interface AccountTypeTotal {
            UUID getAccountId();
            TransactionType getEntryType();
            BigDecimal getTotal();
        }

        interface CategoryTotal {
            UUID getCategoryId();
            String getCategoryName();
            BigDecimal getTotal();
        }

        @Query("""
                select t.type as entryType, sum(t.amount) as total
                from Transaction t
                where t.user.id = :userId
                  and t.transactionDate between :startDate and :endDate
                group by t.type
                """)
        List<TypeTotal> totalsByType(@Param("userId") UUID userId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

        @Query("""
                select t.account.id as accountId, t.type as entryType, sum(t.amount) as total
                from Transaction t
                where t.user.id = :userId
                  and t.transactionDate <= :upTo
                group by t.account.id, t.type
                """)
        List<AccountTypeTotal> totalsByAccountAndType(@Param("userId") UUID userId,
                                                      @Param("upTo") LocalDate upTo);

        @Query("""
                select c.id as categoryId, c.name as categoryName, sum(t.amount) as total
                from Transaction t
                left join t.category c
                where t.user.id = :userId
                  and t.type = :entryType
                  and t.transactionDate between :startDate and :endDate
                group by c.id, c.name
                order by sum(t.amount) desc
                """)
        List<CategoryTotal> totalsByCategory(@Param("userId") UUID userId,
                                             @Param("entryType") TransactionType entryType,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}