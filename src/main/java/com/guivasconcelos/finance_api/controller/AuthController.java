package com.guivasconcelos.finance_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.guivasconcelos.finance_api.dto.LoginRequest;
import com.guivasconcelos.finance_api.dto.RegisterRequest;
import com.guivasconcelos.finance_api.dto.TokenResponse;
import com.guivasconcelos.finance_api.dto.UserResponse;
import com.guivasconcelos.finance_api.model.User;
import com.guivasconcelos.finance_api.service.AuthService;
import com.guivasconcelos.finance_api.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.email(), request.password());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.name(), request.email(), request.password());
        return UserResponse.from(user);
    }
}
