package com.guivasconcelos.finance_api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CategoryTotalResponse(
        UUID categoryId,
        String categoryName,
        BigDecimal total,
        BigDecimal percentage) {}