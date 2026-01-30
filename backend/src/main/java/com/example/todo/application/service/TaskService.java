package com.example.todo.application.service;

import com.example.todo.application.exception.NotFoundException;
import com.example.todo.domain.model.Tag;
import com.example.todo.domain.model.Task;
import com.example.todo.infrastructure.repository.TagRepository;
import com.example.todo.infrastructure.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;

    public TaskService(TaskRepository taskRepository, TagRepository tagRepository) {
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Task getById(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Task not found: " + id));
    }

    @Transactional
    public Task create(String title, String description, List<Long> tagIds) {
        Task task = new Task(title, description);
        if (tagIds != null) {
            for (Long tagId : tagIds) {
                Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new NotFoundException("Tag not found: " + tagId));
                task.addTag(tag);
            }
        }
        return taskRepository.save(task);
    }

    @Transactional
    public Task update(Long id, String title, String description, Boolean completed, List<Long> tagIds) {
        Task task = getById(id);
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
                Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new NotFoundException("Tag not found: " + tagId));
                task.addTag(tag);
            }
        }
        return taskRepository.save(task);
    }

    @Transactional
    public Task toggle(Long id) {
        Task task = getById(id);
        task.setCompleted(!task.isCompleted());
        return taskRepository.save(task);
    }

    @Transactional
    public void delete(Long id) {
        Task task = getById(id);
        taskRepository.delete(task);
    }

    @Transactional
    public Task addTag(Long taskId, Long tagId) {
        Task task = getById(taskId);
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new NotFoundException("Tag not found: " + tagId));
        task.addTag(tag);
        return taskRepository.save(task);
    }

    @Transactional
    public Task removeTag(Long taskId, Long tagId) {
        Task task = getById(taskId);
        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new NotFoundException("Tag not found: " + tagId));
        task.removeTag(tag);
        return taskRepository.save(task);
    }
}
