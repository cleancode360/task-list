package click.cleancode360.todo.tag.infrastructure.controller.web;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.auth.infrastructure.gatewayadapter.spring.CustomUserDetails;
import click.cleancode360.todo.tag.domain.gateway.TagGateway;
import click.cleancode360.todo.shared.pagination.domain.entity.PageResult;
import click.cleancode360.todo.shared.pagination.infrastructure.gatewayadapter.spring.SpringPageMapper;
import click.cleancode360.todo.tag.domain.entity.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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

    private final TagGateway tagGateway;
    private final TagResponseAssembler tagResponseAssembler;
    private final PagedResourcesAssembler<Tag> pagedResourcesAssembler;

    @GetMapping
    public PagedModel<EntityModel<TagResponse>> listTags(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        User user = userDetails.getUser();
        PageResult<Tag> result = tagGateway.getAll(user, SpringPageMapper.toPageRequest(pageable));
        return pagedResourcesAssembler.toModel(SpringPageMapper.toPage(result, pageable), tagResponseAssembler);
    }

    @GetMapping("/{id}")
    public EntityModel<?> getTag(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return tagResponseAssembler.toModel(tagGateway.getById(id, user));
    }

    @PostMapping
    public ResponseEntity<EntityModel<?>> createTag(@Valid @RequestBody TagCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        EntityModel<?> model = tagResponseAssembler.toModel(tagGateway.create(request.name(), user));
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    public EntityModel<?> updateTag(@PathVariable Long id, @Valid @RequestBody TagUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return tagResponseAssembler.toModel(tagGateway.update(id, request.name(), user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        tagGateway.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
