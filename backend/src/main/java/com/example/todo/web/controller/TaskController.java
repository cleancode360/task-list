package com.example.todo.web.controller;

import com.example.todo.application.service.TaskService;
import com.example.todo.web.assembler.TaskResponseAssembler;
import com.example.todo.web.assembler.TaskSummaryAssembler;
import com.example.todo.domain.model.LogPayload;
import com.example.todo.infrastructure.repository.LogRepository;
import com.example.todo.web.dto.TaskCreateRequest;
import com.example.todo.web.dto.TaskSummaryResponse;
import com.example.todo.web.dto.TaskUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskSummaryAssembler taskSummaryAssembler;
    private final TaskResponseAssembler taskResponseAssembler;
    private final LogRepository logRepository;

    @GetMapping
    public CollectionModel<EntityModel<TaskSummaryResponse>> listTasks() {
        long startMs = System.currentTimeMillis();
        CollectionModel<EntityModel<TaskSummaryResponse>> model =
            taskSummaryAssembler.toCollectionModel(taskService.getAll());
        logRepository.info(
                "listTasks",
                LogPayload.builder()
                        .request(null)
                        .response(model)
                        .status(HttpStatus.OK.value())
                        .durationMs(System.currentTimeMillis() - startMs)
                        .build());
        return model;
    }

    @GetMapping("/{id}")
    public EntityModel<?> getTask(@PathVariable Long id) {
        long startMs = System.currentTimeMillis();
        
        EntityModel<?> model = taskResponseAssembler.toModel(taskService.getById(id));

        logRepository.info(
            "getTask",
            LogPayload.builder()
                .request(Map.of("id", id))
                .response(model)
                .status(HttpStatus.OK.value())
                .durationMs(System.currentTimeMillis() - startMs)
                .build()
        );
        return model;
    }

    @PostMapping
    public ResponseEntity<EntityModel<?>> createTask(@Valid @RequestBody TaskCreateRequest request) {
        long startMs = System.currentTimeMillis();

        EntityModel<?> model = taskResponseAssembler.toModel(
            taskService.create(request.title(), request.description(), request.tagIds())
        );

        logRepository.info(
            "createTask",
            LogPayload.builder()
                .request(request)
                .response(model)
                .status(HttpStatus.CREATED.value())
                .durationMs(System.currentTimeMillis() - startMs)
                .build()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    public EntityModel<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest request) {
        long startMs = System.currentTimeMillis();
        EntityModel<?> model = taskResponseAssembler.toModel(
            taskService.update(id, request.title(), request.description(), request.completed(), request.tagIds())
        );

        logRepository.info(
            "updateTask",
            LogPayload.builder()
                .request(Map.of("id", id, "body", request))
                .response(model)
                .status(HttpStatus.OK.value())
                .durationMs(System.currentTimeMillis() - startMs)
                .build()
        );
        return model;
    }

    @PostMapping("/{id}/toggle")
    public EntityModel<?> toggleTask(@PathVariable Long id) {
        long startMs = System.currentTimeMillis();
        EntityModel<?> model = taskResponseAssembler.toModel(taskService.toggle(id));

        logRepository.info(
            "toggleTask",
            LogPayload.builder()
                .request(Map.of("id", id))
                .response(model)
                .status(HttpStatus.OK.value())
                .durationMs(System.currentTimeMillis() - startMs)
                .build()
        );
        return model;
    }

    @PostMapping("/{id}/tags/{tagId}")
    public EntityModel<?> addTag(@PathVariable Long id, @PathVariable Long tagId) {
        long startMs = System.currentTimeMillis();
        EntityModel<?> model = taskResponseAssembler.toModel(taskService.addTag(id, tagId));

        logRepository.info(
            "addTag",
            LogPayload.builder()
                .request(Map.of("id", id, "tagId", tagId))
                .response(model)
                .status(HttpStatus.OK.value())
                .durationMs(System.currentTimeMillis() - startMs)
                .build()
        );
        return model;
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    public EntityModel<?> removeTag(@PathVariable Long id, @PathVariable Long tagId) {
        long startMs = System.currentTimeMillis();
        EntityModel<?> model = taskResponseAssembler.toModel(taskService.removeTag(id, tagId));

        logRepository.info(
            "removeTag",
            LogPayload.builder()
                .request(Map.of("id", id, "tagId", tagId))
                .response(model)
                .status(HttpStatus.OK.value())
                .durationMs(System.currentTimeMillis() - startMs)
                .build()
        );
        return model;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        long startMs = System.currentTimeMillis();
        taskService.delete(id);

        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        logRepository.info(
            "deleteTask",
            LogPayload.builder()
                .request(Map.of("id", id))
                .response(response)
                .status(response.getStatusCode().value())
                .durationMs(System.currentTimeMillis() - startMs)
                .build()
        );
        return response;
    }

}
