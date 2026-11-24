package com.guivasconcelos.finance_api.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.guivasconcelos.finance_api.model.enums.AccountType;

public record AccountBalanceResponse(UUID accountId, String name, AccountType type, BigDecimal balance) {}