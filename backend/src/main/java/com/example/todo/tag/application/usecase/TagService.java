package com.example.todo.tag.application.usecase;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.shared.domain.exception.SharedException;
import com.example.todo.tag.domain.entity.Tag;
import com.example.todo.tag.domain.gateway.TagGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagGateway tagGateway;

    @Transactional(readOnly = true)
    public List<Tag> getAll(User user) {
        return tagGateway.findAllByUser(user);
    }

    @Transactional(readOnly = true)
    public Tag getById(Long id, User user) {
        return tagGateway.findByIdAndUser(id, user)
            .orElseThrow(() -> new SharedException(HttpStatus.NOT_FOUND, "Tag not found: " + id));
    }

    @Transactional
    public Tag create(String name, User user) {
        tagGateway.findByNameIgnoreCaseAndUser(name, user).ifPresent(existing -> {
            throw new SharedException(HttpStatus.CONFLICT, "Tag already exists: " + name);
        });
        Tag tag = new Tag(name);
        tag.setUser(user);
        return tagGateway.save(tag);
    }

    @Transactional
    public Tag update(Long id, String name, User user) {
        Tag tag = getById(id, user);
        tagGateway.findByNameIgnoreCaseAndUser(name, user)
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new SharedException(HttpStatus.CONFLICT, "Tag already exists: " + name);
            });
        tag.setName(name);
        return tagGateway.save(tag);
    }

    @Transactional
    public void delete(Long id, User user) {
        Tag tag = getById(id, user);
        tagGateway.delete(tag);
    }
}
