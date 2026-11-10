package com.guivasconcelos.finance_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.guivasconcelos.finance_api.model.enums.TransactionType;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TransactionRequest(
        @NotNull UUID accountId,
        UUID categoryId,
        @NotNull TransactionType type,
        @NotNull @Positive @Digits(integer = 13, fraction = 2) BigDecimal amount,
        @Size(max = 255) String description,
        @NotNull LocalDate transactionDate
) {}
