package com.example.todo.web.dto;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "tags", itemRelation = "tag")
public class TagResponse {

    private Long id;
    private String name;

    public TagResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
