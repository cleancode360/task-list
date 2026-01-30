package com.example.todo.web.controller;

import com.example.todo.application.service.TagService;
import com.example.todo.web.assembler.TagResponseAssembler;
import com.example.todo.web.dto.TagCreateRequest;
import com.example.todo.web.dto.TagUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;
    private final TagResponseAssembler tagResponseAssembler;

    public TagController(TagService tagService, TagResponseAssembler tagResponseAssembler) {
        this.tagService = tagService;
        this.tagResponseAssembler = tagResponseAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<?>> listTags() {
        return tagResponseAssembler.toCollectionModel(tagService.getAll());
    }

    @GetMapping("/{id}")
    public EntityModel<?> getTag(@PathVariable Long id) {
        return tagResponseAssembler.toModel(tagService.getById(id));
    }

    @PostMapping
    public ResponseEntity<EntityModel<?>> createTag(@Valid @RequestBody TagCreateRequest request) {
        EntityModel<?> model = tagResponseAssembler.toModel(tagService.create(request.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    public EntityModel<?> updateTag(@PathVariable Long id, @Valid @RequestBody TagUpdateRequest request) {
        return tagResponseAssembler.toModel(tagService.update(id, request.getName()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable Long id) {
        tagService.delete(id);
    }
}
