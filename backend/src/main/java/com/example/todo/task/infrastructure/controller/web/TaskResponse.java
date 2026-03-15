package com.example.todo.task.infrastructure.controller.web;

import com.example.todo.tag.infrastructure.controller.web.TagResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "tasks", itemRelation = "task")
public record TaskResponse(
    Long id,
    String title,
    String description,
    boolean completed,
    Instant createdAt,
    Instant updatedAt,
    List<TagResponse> tags
) {}
