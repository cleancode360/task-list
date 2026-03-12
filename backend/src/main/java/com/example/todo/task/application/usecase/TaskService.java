package com.example.todo.task.application.usecase;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.shared.domain.exception.SharedException;
import com.example.todo.tag.domain.entity.Tag;
import com.example.todo.tag.domain.gateway.TagGateway;
import com.example.todo.task.domain.entity.Task;
import com.example.todo.task.domain.gateway.TaskGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskGateway taskGateway;
    private final TagGateway tagGateway;

    @Transactional(readOnly = true)
    public List<Task> getAll(User user) {
        return taskGateway.findAllByUser(user);
    }

    @Transactional(readOnly = true)
    public Task getById(Long id, User user) {
        return taskGateway.findByIdAndUser(id, user)
            .orElseThrow(() -> new SharedException(HttpStatus.NOT_FOUND, "Task not found: " + id));
    }

    @Transactional
    public Task create(String title, String description, List<Long> tagIds, User user) {
        Task task = new Task(title, description);
        task.setUser(user);
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                Tag tag = tagGateway.findByIdAndUser(tagId, user)
                    .orElseThrow(() -> new SharedException(HttpStatus.NOT_FOUND, "Tag not found: " + tagId));
                task.addTag(tag);
            }
        }
        return taskGateway.save(task);
    }

    @Transactional
    public Task update(Long id, String title, String description, Boolean completed, List<Long> tagIds, User user) {
        Task task = getById(id, user);
        if (title != null) {
            task.setTitle(title);
        }
        if (description != null) {
            task.setDescription(description);
        }
        if (completed != null) {
            task.setCompleted(completed);
        }
        if (tagIds != null) {
            task.getTags().forEach(tag -> tag.getTasks().remove(task));
            task.getTags().clear();
            for (Long tagId : tagIds) {
                Tag tag = tagGateway.findByIdAndUser(tagId, user)
                    .orElseThrow(() -> new SharedException(HttpStatus.NOT_FOUND, "Tag not found: " + tagId));
                task.addTag(tag);
            }
        }
        return taskGateway.save(task);
    }

    @Transactional
    public Task toggle(Long id, User user) {
        Task task = getById(id, user);
        task.setCompleted(!task.isCompleted());
        return taskGateway.save(task);
    }

    @Transactional
    public void delete(Long id, User user) {
        Task task = getById(id, user);
        taskGateway.delete(task);
    }

    @Transactional
    public Task addTag(Long taskId, Long tagId, User user) {
        Task task = getById(taskId, user);
        Tag tag = tagGateway.findByIdAndUser(tagId, user)
            .orElseThrow(() -> new SharedException(HttpStatus.NOT_FOUND, "Tag not found: " + tagId));
        task.addTag(tag);
        return taskGateway.save(task);
    }

    @Transactional
    public Task removeTag(Long taskId, Long tagId, User user) {
        Task task = getById(taskId, user);
        Tag tag = tagGateway.findByIdAndUser(tagId, user)
            .orElseThrow(() -> new SharedException(HttpStatus.NOT_FOUND, "Tag not found: " + tagId));
        task.removeTag(tag);
        return taskGateway.save(task);
    }
}
