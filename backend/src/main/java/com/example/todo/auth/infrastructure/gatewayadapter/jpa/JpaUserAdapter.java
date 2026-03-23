package com.example.todo.auth.infrastructure.gatewayadapter.jpa;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.auth.domain.gateway.UserGateway;
import com.example.todo.shared.infrastructure.gatewayadapter.jpa.JpaQueryLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaUserAdapter implements UserGateway {

    private final JpaUserRepository jpaRepository;
    private final JpaQueryLogger queryLogger;

    @Override
    public Optional<User> findByUsername(String username) {
        return queryLogger.queryAndLog("findByUsername", () -> jpaRepository.findByUsername(username));
    }

    @Override
    public User save(User user) {
        return queryLogger.queryAndLog("save", () -> jpaRepository.save(user));
    }
}
