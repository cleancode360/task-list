package com.example.todo.web.controller;

import com.example.todo.application.service.TaskService;
import com.example.todo.web.assembler.TaskResponseAssembler;
import com.example.todo.web.assembler.TaskSummaryAssembler;
import com.example.todo.web.dto.TaskCreateRequest;
import com.example.todo.web.dto.TaskUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskSummaryAssembler taskSummaryAssembler;
    private final TaskResponseAssembler taskResponseAssembler;

    public TaskController(TaskService taskService,
                          TaskSummaryAssembler taskSummaryAssembler,
                          TaskResponseAssembler taskResponseAssembler) {
        this.taskService = taskService;
        this.taskSummaryAssembler = taskSummaryAssembler;
        this.taskResponseAssembler = taskResponseAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<?>> listTasks() {
        return taskSummaryAssembler.toCollectionModel(taskService.getAll());
    }

    @GetMapping("/{id}")
    public EntityModel<?> getTask(@PathVariable Long id) {
        return taskResponseAssembler.toModel(taskService.getById(id));
    }

    @PostMapping
    public ResponseEntity<EntityModel<?>> createTask(@Valid @RequestBody TaskCreateRequest request) {
        EntityModel<?> model = taskResponseAssembler.toModel(
            taskService.create(request.getTitle(), request.getDescription(), request.getTagIds())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    public EntityModel<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest request) {
        return taskResponseAssembler.toModel(
            taskService.update(id, request.getTitle(), request.getDescription(), request.getCompleted(), request.getTagIds())
        );
    }

    @PostMapping("/{id}/toggle")
    public EntityModel<?> toggleTask(@PathVariable Long id) {
        return taskResponseAssembler.toModel(taskService.toggle(id));
    }

    @PostMapping("/{id}/tags/{tagId}")
    public EntityModel<?> addTag(@PathVariable Long id, @PathVariable Long tagId) {
        return taskResponseAssembler.toModel(taskService.addTag(id, tagId));
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    public EntityModel<?> removeTag(@PathVariable Long id, @PathVariable Long tagId) {
        return taskResponseAssembler.toModel(taskService.removeTag(id, tagId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.delete(id);
    }
}
