package click.cleancode360.todo.tag.infrastructure.controller.web;

import java.util.UUID;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "tags", itemRelation = "tag")
public record TagResponse(UUID id, String name) {}
