package com.example.todo.auth.application.usecase;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.auth.domain.gateway.PasswordHasher;
import com.example.todo.auth.domain.gateway.UserGateway;
import com.example.todo.shared.domain.exception.SharedException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService {

    private final UserGateway userGateway;
    private final PasswordHasher passwordHasher;

    public User register(String username, String password) {
        userGateway.findByUsername(username).ifPresent(existing -> {
            throw new SharedException(409, "Username already taken: " + username);
        });
        User user = new User(username, passwordHasher.hash(password));
        return userGateway.save(user);
    }
}
