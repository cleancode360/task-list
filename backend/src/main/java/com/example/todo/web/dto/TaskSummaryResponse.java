package com.example.todo.web.dto;

import java.util.List;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "tasks", itemRelation = "task")
public record TaskSummaryResponse(
    Long id,
    String title,
    boolean completed,
    List<TagResponse> tags
) {}
