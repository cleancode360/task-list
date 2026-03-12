package com.example.todo.auth.infrastructure.gatewayadapter;

import com.example.todo.auth.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
