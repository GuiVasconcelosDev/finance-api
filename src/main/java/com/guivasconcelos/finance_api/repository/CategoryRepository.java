package com.guivasconcelos.finance_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guivasconcelos.finance_api.model.Category;
import com.guivasconcelos.finance_api.model.enums.TransactionType;


public interface CategoryRepository extends JpaRepository<Category, UUID>{
    List<Category> findAllByUserId(UUID userId);

    Optional<Category> findByIdAndUserId(UUID id, UUID userId);

    List<Category> findAllByUserIdOrderByNameAsc(UUID userId);

    boolean existsByUserIdAndNameAndType(UUID userId, String name, TransactionType type);   
}
