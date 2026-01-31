package com.example.todo.web.dto;

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
