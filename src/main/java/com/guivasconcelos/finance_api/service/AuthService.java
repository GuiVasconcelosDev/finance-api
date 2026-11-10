package com.guivasconcelos.finance_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.guivasconcelos.finance_api.dto.TokenResponse;
import com.guivasconcelos.finance_api.exception.InvalidCredentialsException;
import com.guivasconcelos.finance_api.model.User;
import com.guivasconcelos.finance_api.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final String dummyHash;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.dummyHash = passwordEncoder.encode("SX7lecOrWeR21LOh1");
    }

    public TokenResponse login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email.trim().toLowerCase()).orElse(null);

        String hashToCheck = (user != null) ? user.getPasswordHash() : dummyHash;
        boolean passwordMatches = passwordEncoder.matches(rawPassword, hashToCheck);

        if (user == null || !passwordMatches) {
            throw new InvalidCredentialsException();
        }

        return new TokenResponse(tokenService.generate(user), "Bearer", tokenService.expiresInSeconds());
    }
}
