package com.guivasconcelos.finance_api.dto;

import java.util.UUID;

import com.guivasconcelos.finance_api.model.User;

public record UserResponse(UUID id, String name, String email) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

}
