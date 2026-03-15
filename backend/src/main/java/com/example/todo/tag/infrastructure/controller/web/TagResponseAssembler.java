package com.example.todo.tag.infrastructure.controller.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.todo.tag.domain.entity.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class TagResponseAssembler implements RepresentationModelAssembler<Tag, EntityModel<TagResponse>> {

    @Override
    public EntityModel<TagResponse> toModel(Tag tag) {
        TagResponse response = new TagResponse(tag.getId(), tag.getName());
        return EntityModel.of(
            response,
            linkTo(methodOn(RESTTagController.class).getTag(tag.getId(), null)).withSelfRel(),
            linkTo(methodOn(RESTTagController.class).updateTag(tag.getId(), null, null)).withRel("update"),
            linkTo(methodOn(RESTTagController.class).deleteTag(tag.getId(), null)).withRel("delete"),
            linkTo(methodOn(RESTTagController.class).listTags(null)).withRel("collection")
        );
    }
}
