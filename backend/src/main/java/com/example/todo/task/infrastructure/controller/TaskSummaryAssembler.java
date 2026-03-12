package com.example.todo.task.infrastructure.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.todo.tag.infrastructure.controller.TagResponse;
import com.example.todo.task.domain.entity.Task;
import java.util.stream.Collectors;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class TaskSummaryAssembler implements RepresentationModelAssembler<Task, EntityModel<TaskSummaryResponse>> {

    @Override
    public EntityModel<TaskSummaryResponse> toModel(Task task) {
        TaskSummaryResponse response = new TaskSummaryResponse(
            task.getId(),
            task.getTitle(),
            task.isCompleted(),
            task.getTags().stream()
                .map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .collect(Collectors.toList())
        );

        return EntityModel.of(
            response,
            linkTo(methodOn(TaskController.class).getTask(task.getId(), null)).withSelfRel(),
            linkTo(methodOn(TaskController.class).toggleTask(task.getId(), null)).withRel("toggle"),
            linkTo(methodOn(TaskController.class).updateTask(task.getId(), null, null)).withRel("update"),
            linkTo(methodOn(TaskController.class).deleteTask(task.getId(), null)).withRel("delete")
        );
    }
}
