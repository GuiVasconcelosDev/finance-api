package com.guivasconcelos.finance_api.dto;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {}
