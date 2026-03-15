package com.example.todo.task.infrastructure.gatewayadapter.jpa;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.task.domain.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface TaskJpaRepository extends JpaRepository<Task, Long> {
    @EntityGraph(attributePaths = "tags")
    List<Task> findAllByUser(User user);

    @EntityGraph(attributePaths = "tags")
    Optional<Task> findByIdAndUser(Long id, User user);
}
