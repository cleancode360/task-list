package com.example.todo.task.infrastructure.controller;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.auth.infrastructure.gatewayadapter.CustomUserDetails;
import com.example.todo.task.application.usecase.TaskService;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskSummaryAssembler taskSummaryAssembler;
    private final TaskResponseAssembler taskResponseAssembler;

    @GetMapping
    public CollectionModel<EntityModel<TaskSummaryResponse>> listTasks(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return taskSummaryAssembler.toCollectionModel(Objects.requireNonNull(taskService.getAll(user)));
    }

    @GetMapping("/{id}")
    public EntityModel<?> getTask(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return taskResponseAssembler.toModel(taskService.getById(id, user));
    }

    @PostMapping
    public ResponseEntity<EntityModel<?>> createTask(@Valid @RequestBody TaskCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        EntityModel<?> model = taskResponseAssembler.toModel(
            taskService.create(request.title(), request.description(), request.tagIds(), user)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    public EntityModel<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return taskResponseAssembler.toModel(
            taskService.update(id, request.title(), request.description(), request.completed(), request.tagIds(), user)
        );
    }

    @PostMapping("/{id}/toggle")
    public EntityModel<?> toggleTask(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return taskResponseAssembler.toModel(taskService.toggle(id, user));
    }

    @PostMapping("/{id}/tags/{tagId}")
    public EntityModel<?> addTag(@PathVariable Long id, @PathVariable Long tagId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return taskResponseAssembler.toModel(taskService.addTag(id, tagId, user));
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    public EntityModel<?> removeTag(@PathVariable Long id, @PathVariable Long tagId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return taskResponseAssembler.toModel(taskService.removeTag(id, tagId, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        taskService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
