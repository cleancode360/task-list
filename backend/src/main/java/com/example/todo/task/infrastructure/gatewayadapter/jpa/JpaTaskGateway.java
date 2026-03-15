package com.example.todo.task.infrastructure.gatewayadapter.jpa;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.task.domain.entity.Task;
import com.example.todo.task.domain.gateway.TaskGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaTaskGateway implements TaskGateway {

    private final JpaTaskRepository jpaRepository;

    @Override
    public List<Task> findAllByUser(User user) {
        return jpaRepository.findAllByUser(user);
    }

    @Override
    public Optional<Task> findByIdAndUser(Long id, User user) {
        return jpaRepository.findByIdAndUser(id, user);
    }

    @Override
    public Task save(Task task) {
        return jpaRepository.save(task);
    }

    @Override
    public void delete(Task task) {
        jpaRepository.delete(task);
    }
}
