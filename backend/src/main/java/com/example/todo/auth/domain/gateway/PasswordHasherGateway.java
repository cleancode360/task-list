package com.example.todo.auth.domain.gateway;

public interface PasswordHasherGateway {
    String hash(String rawPassword);
}
