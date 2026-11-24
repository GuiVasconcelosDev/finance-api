package com.guivasconcelos.finance_api.dto;

import java.math.BigDecimal;
import java.util.List;

public record BalancesResponse(BigDecimal total, List<AccountBalanceResponse> accounts) {}