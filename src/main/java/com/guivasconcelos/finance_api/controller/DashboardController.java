package com.guivasconcelos.finance_api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.guivasconcelos.finance_api.dto.*;
import com.guivasconcelos.finance_api.exception.BussinesRuleException;
import com.guivasconcelos.finance_api.exception.CurrentUser;
import com.guivasconcelos.finance_api.model.enums.TransactionType;
import com.guivasconcelos.finance_api.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public SummaryResponse summary(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to)
            throws BussinesRuleException {
        return dashboardService.summary(CurrentUser.id(jwt), startOf(from), endOf(to));
    }

    @GetMapping("/balances")
    public BalancesResponse balances(@AuthenticationPrincipal Jwt jwt) {
        return dashboardService.balances(CurrentUser.id(jwt), LocalDate.now());
    }

    @GetMapping("/by-category")
    public List<CategoryTotalResponse> byCategory(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "EXPENSE") TransactionType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to)
            throws BussinesRuleException {
        return dashboardService.byCategory(CurrentUser.id(jwt), type, startOf(from), endOf(to));
    }

    private LocalDate startOf(LocalDate from) {
        return from != null ? from : LocalDate.now().withDayOfMonth(1);
    }

    private LocalDate endOf(LocalDate to) {
        LocalDate today = LocalDate.now();
        return to != null ? to : today.withDayOfMonth(today.lengthOfMonth());
    }
}