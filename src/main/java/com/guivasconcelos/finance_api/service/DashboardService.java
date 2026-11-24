package com.guivasconcelos.finance_api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guivasconcelos.finance_api.dto.*;
import com.guivasconcelos.finance_api.exception.BussinesRuleException;
import com.guivasconcelos.finance_api.model.Account;
import com.guivasconcelos.finance_api.model.enums.TransactionType;
import com.guivasconcelos.finance_api.repository.AccountRepository;
import com.guivasconcelos.finance_api.repository.TransactionRepository;

@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public DashboardService(TransactionRepository transactionRepository,
                            AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public SummaryResponse summary(UUID userId, LocalDate from, LocalDate to) throws BussinesRuleException {
        validateRange(from, to);

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        for (TransactionRepository.TypeTotal row : transactionRepository.totalsByType(userId, from, to)) {
            if (row.getEntryType() == TransactionType.INCOME) {
                income = row.getTotal();
            } else {
                expense = row.getTotal();
            }
        }
        return new SummaryResponse(from, to, income, expense, income.subtract(expense));
    }

    @Transactional(readOnly = true)
    public BalancesResponse balances(UUID userId, LocalDate asOf) {
        List<Account> accounts = accountRepository.findAllByUserIdOrderByNameAsc(userId);

        Map<UUID, BigDecimal> movement = new HashMap<>();
        for (TransactionRepository.AccountTypeTotal row
                : transactionRepository.totalsByAccountAndType(userId, asOf)) {
            BigDecimal signed = (row.getEntryType() == TransactionType.INCOME)
                    ? row.getTotal()
                    : row.getTotal().negate();
            movement.merge(row.getAccountId(), signed, BigDecimal::add);
        }

        List<AccountBalanceResponse> list = accounts.stream()
                .map(a -> new AccountBalanceResponse(
                        a.getId(), a.getName(), a.getType(),
                        a.getInitialBalance().add(movement.getOrDefault(a.getId(), BigDecimal.ZERO))))
                .toList();

        BigDecimal total = list.stream()
                .map(AccountBalanceResponse::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BalancesResponse(total, list);
    }

    @Transactional(readOnly = true)
    public List<CategoryTotalResponse> byCategory(UUID userId, TransactionType type,
                                                  LocalDate from, LocalDate to) throws BussinesRuleException {
        validateRange(from, to);

        List<TransactionRepository.CategoryTotal> rows =
                transactionRepository.totalsByCategory(userId, type, from, to);

        BigDecimal grandTotal = rows.stream()
                .map(TransactionRepository.CategoryTotal::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return rows.stream()
                .map(r -> new CategoryTotalResponse(
                        r.getCategoryId(),
                        r.getCategoryName() == null ? "Sem categoria" : r.getCategoryName(),
                        r.getTotal(),
                        percent(r.getTotal(), grandTotal)))
                .toList();
    }

    private BigDecimal percent(BigDecimal part, BigDecimal whole) {
        if (whole.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return part.multiply(BigDecimal.valueOf(100)).divide(whole, 1, RoundingMode.HALF_UP);
    }

    private void validateRange(LocalDate from, LocalDate to) throws BussinesRuleException {
        if (from.isAfter(to)) {
            throw new BussinesRuleException("A data inicial não pode ser depois da final");
        }
    }
}