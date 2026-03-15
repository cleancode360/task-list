package com.example.todo.task.infrastructure.controller.web;

import com.example.todo.tag.infrastructure.controller.web.TagResponse;
import java.util.List;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "tasks", itemRelation = "task")
public record TaskSummaryResponse(
    Long id,
    String title,
    boolean completed,
    List<TagResponse> tags
) {}
