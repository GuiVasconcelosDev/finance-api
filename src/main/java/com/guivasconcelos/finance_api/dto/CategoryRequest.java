package com.guivasconcelos.finance_api.dto;
import com.guivasconcelos.finance_api.model.enums.TransactionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
    @NotBlank @Size(max = 100) String name,
    @NotNull TransactionType type
) {}
