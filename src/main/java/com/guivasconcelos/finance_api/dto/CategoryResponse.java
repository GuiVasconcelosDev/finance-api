package com.guivasconcelos.finance_api.dto;

import java.time.Instant;
import java.util.UUID;

import com.guivasconcelos.finance_api.model.Category;
import com.guivasconcelos.finance_api.model.enums.TransactionType;


public record CategoryResponse(UUID id, String name, TransactionType type, Instant createdAt) {
    public static CategoryResponse fromEntity(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getType(), c.getCreatedAt());
    }

}
