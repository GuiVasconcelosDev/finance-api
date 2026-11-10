package com.guivasconcelos.finance_api.dto;

import java.math.BigDecimal;

import com.guivasconcelos.finance_api.model.enums.AccountType;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountRequest(
    @NotBlank @Size(max = 100) String name,
    @NotNull AccountType type,
    @NotNull @Digits(integer = 13, fraction = 2) BigDecimal initialBalance
) {}
