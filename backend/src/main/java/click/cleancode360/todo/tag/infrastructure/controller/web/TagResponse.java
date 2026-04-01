package click.cleancode360.todo.tag.infrastructure.controller.web;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "tags", itemRelation = "tag")
public record TagResponse(Long id, String name) {}
