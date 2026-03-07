package com.example.todo.application.service;

import com.example.todo.application.exception.ApiException;
import com.example.todo.domain.model.Tag;
import com.example.todo.domain.model.Task;
import com.example.todo.domain.model.User;
import com.example.todo.infrastructure.repository.TagRepository;
import com.example.todo.infrastructure.repository.TaskRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<Task> getAll(User user) {
        return taskRepository.findAllByUser(user);
    }

    @Transactional(readOnly = true)
    public Task getById(Long id, User user) {
        return taskRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task not found: " + id));
    }

    @Transactional
    public Task create(String title, String description, List<Long> tagIds, User user) {
        Task task = new Task(title, description);
        task.setUser(user);
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                Tag tag = tagRepository.findByIdAndUser(tagId, user)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Tag not found: " + tagId));
                task.addTag(tag);
            }
        }
        return taskRepository.save(task);
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
                Tag tag = tagRepository.findByIdAndUser(tagId, user)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Tag not found: " + tagId));
                task.addTag(tag);
            }
        }
        return taskRepository.save(task);
    }

    @Transactional
    public Task toggle(Long id, User user) {
        Task task = getById(id, user);
        task.setCompleted(!task.isCompleted());
        return taskRepository.save(task);
    }

    @Transactional
    public void delete(Long id, User user) {
        Task task = getById(id, user);
        taskRepository.delete(task);
    }

    @Transactional
    public Task addTag(Long taskId, Long tagId, User user) {
        Task task = getById(taskId, user);
        Tag tag = tagRepository.findByIdAndUser(tagId, user)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Tag not found: " + tagId));
        task.addTag(tag);
        return taskRepository.save(task);
    }

    @Transactional
    public Task removeTag(Long taskId, Long tagId, User user) {
        Task task = getById(taskId, user);
        Tag tag = tagRepository.findByIdAndUser(tagId, user)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Tag not found: " + tagId));
        task.removeTag(tag);
        return taskRepository.save(task);
    }
}
