package com.example.todo.tag.infrastructure.controller.web;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.auth.infrastructure.security.CustomUserDetails;
import com.example.todo.tag.application.usecase.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class RESTTagController {

    private final TagService tagService;
    private final TagResponseAssembler tagResponseAssembler;

    @GetMapping
    public CollectionModel<EntityModel<TagResponse>> listTags(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return tagResponseAssembler.toCollectionModel(tagService.getAll(user));
    }

    @GetMapping("/{id}")
    public EntityModel<?> getTag(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return tagResponseAssembler.toModel(tagService.getById(id, user));
    }

    @PostMapping
    public ResponseEntity<EntityModel<?>> createTag(@Valid @RequestBody TagCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        EntityModel<?> model = tagResponseAssembler.toModel(tagService.create(request.name(), user));
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    public EntityModel<?> updateTag(@PathVariable Long id, @Valid @RequestBody TagUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return tagResponseAssembler.toModel(tagService.update(id, request.name(), user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        tagService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
