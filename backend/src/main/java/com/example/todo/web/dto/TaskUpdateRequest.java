package com.example.todo.web.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

public class TaskUpdateRequest {

    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    private Boolean completed;

    private List<Long> tagIds;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
}
