package com.example.todo.web.dto;

import java.util.List;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "tasks", itemRelation = "task")
public class TaskSummaryResponse {

    private Long id;
    private String title;
    private boolean completed;
    private List<TagResponse> tags;

    public TaskSummaryResponse(Long id, String title, boolean completed, List<TagResponse> tags) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public List<TagResponse> getTags() {
        return tags;
    }
}
