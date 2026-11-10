package com.guivasconcelos.finance_api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.guivasconcelos.finance_api.model.Account;
import com.guivasconcelos.finance_api.model.enums.AccountType;

public record AccountResponse(UUID id, String name, AccountType type,
                            BigDecimal initialBalance, Instant createAt) {

    public static AccountResponse from(Account a) {
        return new AccountResponse(a.getId(), a.getName(), a.getType(), a.getInitialBalance(), a.getCreatedAt());
    }                            

}
