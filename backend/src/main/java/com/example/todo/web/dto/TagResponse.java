package com.example.todo.web.dto;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "tags", itemRelation = "tag")
public record TagResponse(Long id, String name) {}
