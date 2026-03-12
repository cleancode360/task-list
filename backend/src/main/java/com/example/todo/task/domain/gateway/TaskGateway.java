package com.example.todo.task.domain.gateway;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.task.domain.entity.Task;
import java.util.List;
import java.util.Optional;

public interface TaskGateway {
    List<Task> findAllByUser(User user);
    Optional<Task> findByIdAndUser(Long id, User user);
    Task save(Task task);
    void delete(Task task);
}
