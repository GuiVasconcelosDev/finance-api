package com.guivasconcelos.finance_api.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guivasconcelos.finance_api.model.Account;

public interface AccountRepository extends JpaRepository<Account, UUID>{

    List<Account> findAllByUserId(UUID userId);
    List<Account> findAllByUserIdOrderByNameAsc(UUID userId);

    Optional<Account> findByIdAndUserId(UUID id, UUID userId);

}
