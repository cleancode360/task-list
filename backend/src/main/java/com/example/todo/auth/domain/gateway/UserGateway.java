package com.example.todo.auth.domain.gateway;

import com.example.todo.auth.domain.entity.User;
import java.util.Optional;

public interface UserGateway {
    Optional<User> findByUsername(String username);
    User save(User user);
}
