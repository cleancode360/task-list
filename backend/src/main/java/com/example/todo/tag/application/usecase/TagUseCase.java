package com.example.todo.tag.application.usecase;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.shared.domain.exception.SharedException;
import com.example.todo.tag.domain.entity.Tag;
import com.example.todo.tag.domain.gateway.TagGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class TagUseCase {

    private final TagGateway tagGateway;

    public List<Tag> getAll(User user) {
        return tagGateway.findAllByUser(user);
    }

    public Page<Tag> getAll(User user, Pageable pageable) {
        return tagGateway.findAllByUser(user, pageable);
    }

    public Tag getById(Long id, User user) {
        return tagGateway.findByIdAndUser(id, user)
            .orElseThrow(() -> new SharedException(404, "Tag not found: " + id));
    }

    public Tag create(String name, User user) {
        tagGateway.findByNameIgnoreCaseAndUser(name, user).ifPresent(existing -> {
            throw new SharedException(409, "Tag already exists: " + name);
        });
        Tag tag = new Tag(name);
        tag.setUser(user);
        return tagGateway.save(tag);
    }

    public Tag update(Long id, String name, User user) {
        Tag tag = getById(id, user);
        tagGateway.findByNameIgnoreCaseAndUser(name, user)
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new SharedException(409, "Tag already exists: " + name);
            });
        tag.setName(name);
        return tagGateway.save(tag);
    }

    public void delete(Long id, User user) {
        Tag tag = getById(id, user);
        tagGateway.delete(tag);
    }
}
