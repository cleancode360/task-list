package com.example.todo.task.infrastructure.controller.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.todo.tag.infrastructure.controller.web.TagResponse;
import com.example.todo.task.domain.entity.Task;
import java.util.stream.Collectors;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class TaskResponseAssembler implements RepresentationModelAssembler<Task, EntityModel<TaskResponse>> {

    @Override
    public EntityModel<TaskResponse> toModel(Task task) {
        TaskResponse response = new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.isCompleted(),
            task.getCreatedAt(),
            task.getUpdatedAt(),
            task.getTags().stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .collect(Collectors.toList())
        );

        return EntityModel.of(
            response,
            linkTo(methodOn(RESTTaskController.class).getTask(task.getId(), null)).withSelfRel(),
            linkTo(methodOn(RESTTaskController.class).updateTask(task.getId(), null, null)).withRel("update"),
            linkTo(methodOn(RESTTaskController.class).deleteTask(task.getId(), null)).withRel("delete"),
            linkTo(methodOn(RESTTaskController.class).toggleTask(task.getId(), null)).withRel("toggle"),
            linkTo(methodOn(RESTTaskController.class).listTasks(null)).withRel("collection")
        );
    }
}
