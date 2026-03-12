package com.example.todo.task.infrastructure.gatewayadapter;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.task.domain.entity.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

interface TaskJpaRepository extends JpaRepository<Task, Long> {
    @EntityGraph(attributePaths = "tags")
    List<Task> findAllByUser(User user);

    @EntityGraph(attributePaths = "tags")
    Optional<Task> findByIdAndUser(Long id, User user);
}
