package com.guivasconcelos.finance_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.guivasconcelos.finance_api.dto.AccountRequest;
import com.guivasconcelos.finance_api.exception.ConflictException;
import com.guivasconcelos.finance_api.exception.ResourcesNotFoundException;
import com.guivasconcelos.finance_api.model.Account;
import com.guivasconcelos.finance_api.model.User;
import com.guivasconcelos.finance_api.repository.AccountRepository;
import com.guivasconcelos.finance_api.repository.TransactionRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository,
                          UserService userService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    @Transactional
    public Account create(UUID userId, AccountRequest req) {
        User user = userService.getById(userId);
        return accountRepository.save(
            new Account(user, req.name().trim(), req.type(), req.initialBalance()));
    }

    @Transactional
    public List<Account> list(UUID userId) {
        return accountRepository.findAllByUserIdOrderByNameAsc(userId);
    }

    @Transactional
    public Account get(UUID user, UUID id) {
        return accountRepository.findByIdAndUserId(id, user)
            .orElseThrow(() -> new ResourcesNotFoundException("Conta não encontrada"));
    }
    
    @Transactional
    public Account update(UUID userId, UUID id, AccountRequest req) {
        Account account = get(userId, id);
        account.update(req.name().trim(), req.type(), req.initialBalance());
        return account;
    }

    @Transactional
    public void delete(UUID userID, UUID id) {
        Account account = get(userID, id);
        if(transactionRepository.existsByAccountId(id)) {
            throw new ConflictException("A conta possui transações e não pode ser apagados");
        }
        accountRepository.delete(account);

    }
}
