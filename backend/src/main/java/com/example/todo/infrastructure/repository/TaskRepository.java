package com.example.todo.infrastructure.repository;

import com.example.todo.domain.model.Task;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Override
    @EntityGraph(attributePaths = "tags")
    Optional<Task> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "tags")
    java.util.List<Task> findAll();
}
