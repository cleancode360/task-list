package com.example.todo.task.infrastructure.gatewayadapter.jpa;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.task.domain.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface JpaTaskRepository extends JpaRepository<Task, Long> {
    @EntityGraph(attributePaths = "tags")
    List<Task> findAllByUser(User user);

    @EntityGraph(attributePaths = "tags")
    Page<Task> findAllByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = "tags")
    Optional<Task> findByIdAndUser(Long id, User user);
}
