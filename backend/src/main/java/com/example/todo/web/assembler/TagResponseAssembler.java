package com.example.todo.web.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.todo.domain.model.Tag;
import com.example.todo.web.controller.TagController;
import com.example.todo.web.dto.TagResponse;
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
            linkTo(methodOn(TagController.class).getTag(tag.getId(), null)).withSelfRel(),
            linkTo(methodOn(TagController.class).updateTag(tag.getId(), null, null)).withRel("update"),
            linkTo(methodOn(TagController.class).deleteTag(tag.getId(), null)).withRel("delete"),
            linkTo(methodOn(TagController.class).listTags(null)).withRel("collection")
        );
    }
}
