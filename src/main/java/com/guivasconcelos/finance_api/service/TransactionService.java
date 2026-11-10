package com.guivasconcelos.finance_api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guivasconcelos.finance_api.dto.TransactionRequest;
import com.guivasconcelos.finance_api.dto.TransactionResponse;
import com.guivasconcelos.finance_api.exception.BussinesRuleException;
import com.guivasconcelos.finance_api.exception.ResourcesNotFoundException;
import com.guivasconcelos.finance_api.model.Account;
import com.guivasconcelos.finance_api.model.Category;
import com.guivasconcelos.finance_api.model.Transaction;
import com.guivasconcelos.finance_api.model.User;
import com.guivasconcelos.finance_api.model.enums.TransactionType;
import com.guivasconcelos.finance_api.repository.AccountRepository;
import com.guivasconcelos.finance_api.repository.CategoryRepository;
import com.guivasconcelos.finance_api.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository,
                              UserService userService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
    }

    @Transactional
    public TransactionResponse create(UUID userId, TransactionRequest req) throws BussinesRuleException {
        User user = userService.getById(userId);
        Account account = findAccount(userId, req.accountId());
        Category category = findCategory(userId, req.categoryId(), req.type());

        Transaction saved = transactionRepository.save(new Transaction(
                user, account, category, req.type(), req.amount(),
                clean(req.description()), req.transactionDate()));
        return TransactionResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> list(UUID userId, LocalDate from, LocalDate to) throws BussinesRuleException {
        if (from.isAfter(to)) {
            throw new BussinesRuleException("A data inicial não pode ser depois da final");
        }
        return transactionRepository
                .findAllByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, from, to)
                .stream().map(TransactionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse get(UUID userId, UUID id) {
        return TransactionResponse.from(findTransaction(userId, id));
    }

    @Transactional
    public TransactionResponse update(UUID userId, UUID id, TransactionRequest req) throws BussinesRuleException {
        Transaction transaction = findTransaction(userId, id);
        Account account = findAccount(userId, req.accountId());
        Category category = findCategory(userId, req.categoryId(), req.type());

        transaction.update(account, category, req.type(), req.amount(),
                clean(req.description()), req.transactionDate());
        return TransactionResponse.from(transaction);
    }

    @Transactional
    public void delete(UUID userId, UUID id) {
        transactionRepository.delete(findTransaction(userId, id));
    }

    private Transaction findTransaction(UUID userId, UUID id) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourcesNotFoundException("Transação não encontrada"));
    }

    private Account findAccount(UUID userId, UUID accountId) {
        return accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ResourcesNotFoundException("Conta não encontrada"));
    }

    private Category findCategory(UUID userId, UUID categoryId, TransactionType type) throws BussinesRuleException {
        if (categoryId == null) {
            return null;
        }
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourcesNotFoundException("Categoria não encontrada"));
        if (category.getType() != type) {
            throw new BussinesRuleException("O tipo da categoria não corresponde ao tipo da transação");
        }
        return category;
    }

    private String clean(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }
}
