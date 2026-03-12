package com.example.todo.auth.domain.gateway;

public interface PasswordHasher {
    String hash(String rawPassword);
}
