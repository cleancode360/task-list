package click.cleancode360.todo.task.infrastructure.controller.web;

import click.cleancode360.todo.tag.infrastructure.controller.web.TagResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "tasks", itemRelation = "task")
public record TaskSummaryResponse(
    UUID id,
    String title,
    boolean completed,
    List<TagResponse> tags
) {}
