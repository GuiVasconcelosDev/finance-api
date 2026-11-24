package com.guivasconcelos.finance_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SummaryResponse(LocalDate from, LocalDate to,
                              BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal net) {}