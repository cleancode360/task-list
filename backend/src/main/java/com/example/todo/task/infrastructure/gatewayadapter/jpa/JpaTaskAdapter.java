package com.example.todo.task.infrastructure.gatewayadapter.jpa;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.shared.infrastructure.gatewayadapter.jpa.JpaQueryLogger;
import com.example.todo.task.domain.entity.Task;
import com.example.todo.task.domain.gateway.TaskGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaTaskAdapter implements TaskGateway {

    private final JpaTaskRepository jpaRepository;
    private final JpaQueryLogger queryLogger;

    @Override
    public List<Task> findAllByUser(User user) {
        return queryLogger.queryAndLog("findAllByUser", () -> jpaRepository.findAllByUser(user));
    }

    @Override
    public Page<Task> findAllByUser(User user, Pageable pageable) {
        return queryLogger.queryAndLog("findAllByUser(pageable)", () -> jpaRepository.findAllByUser(user, pageable));
    }

    @Override
    public Optional<Task> findByIdAndUser(Long id, User user) {
        return queryLogger.queryAndLog("findByIdAndUser", () -> jpaRepository.findByIdAndUser(id, user));
    }

    @Override
    public Task save(Task task) {
        return queryLogger.queryAndLog("save", () -> jpaRepository.save(task));
    }

    @Override
    public void delete(Task task) {
        queryLogger.queryAndLog("delete", () -> {
            jpaRepository.delete(task);
            return null;
        });
    }
}
