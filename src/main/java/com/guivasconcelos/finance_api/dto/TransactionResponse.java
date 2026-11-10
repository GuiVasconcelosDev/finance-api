package com.guivasconcelos.finance_api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.guivasconcelos.finance_api.model.Transaction;
import com.guivasconcelos.finance_api.model.enums.TransactionType;

public record TransactionResponse(UUID id, UUID accountId, UUID categoryId, TransactionType type,
                                  BigDecimal amount, String description,
                                  LocalDate transactionDate, Instant createdAt) {

    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getAccount().getId(),
                t.getCategory() == null ? null : t.getCategory().getId(),
                t.getType(), t.getAmount(), t.getDescription(),
                t.getTransactionDate(), t.getCreatedAt());
    }
}
