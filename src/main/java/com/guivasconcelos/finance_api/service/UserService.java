package com.guivasconcelos.finance_api.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guivasconcelos.finance_api.exception.EmailAlreadyUsedException;
import com.guivasconcelos.finance_api.exception.InvalidCredentialsException;
import com.guivasconcelos.finance_api.model.User;
import com.guivasconcelos.finance_api.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String name, String email, String rawPassword) {
        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyUsedException(normalizedEmail);
        }
        String hash = passwordEncoder.encode(rawPassword);
        return userRepository.save(new User(name.trim(), normalizedEmail, hash));
    }

    @Transactional(readOnly = true)
    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(InvalidCredentialsException::new);
    }
}
