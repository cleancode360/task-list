package com.example.todo.web.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.todo.domain.model.Task;
import com.example.todo.web.controller.TaskController;
import com.example.todo.web.dto.TagResponse;
import com.example.todo.web.dto.TaskSummaryResponse;
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
            linkTo(methodOn(TaskController.class).getTask(task.getId())).withSelfRel(),
            linkTo(methodOn(TaskController.class).toggleTask(task.getId())).withRel("toggle"),
            linkTo(methodOn(TaskController.class).updateTask(task.getId(), null)).withRel("update"),
            linkTo(methodOn(TaskController.class).deleteTask(task.getId())).withRel("delete")
        );
    }
}
