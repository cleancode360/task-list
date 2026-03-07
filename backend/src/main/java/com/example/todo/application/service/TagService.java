package com.example.todo.application.service;

import com.example.todo.application.exception.ApiException;
import com.example.todo.domain.model.Tag;
import com.example.todo.domain.model.User;
import com.example.todo.infrastructure.repository.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<Tag> getAll(User user) {
        return tagRepository.findAllByUser(user);
    }

    @Transactional(readOnly = true)
    public Tag getById(Long id, User user) {
        return tagRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Tag not found: " + id));
    }

    @Transactional
    public Tag create(String name, User user) {
        tagRepository.findByNameIgnoreCaseAndUser(name, user).ifPresent(existing -> {
            throw new ApiException(HttpStatus.CONFLICT, "Tag already exists: " + name);
        });
        Tag tag = new Tag(name);
        tag.setUser(user);
        return tagRepository.save(tag);
    }

    @Transactional
    public Tag update(Long id, String name, User user) {
        Tag tag = getById(id, user);
        tagRepository.findByNameIgnoreCaseAndUser(name, user)
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new ApiException(HttpStatus.CONFLICT, "Tag already exists: " + name);
            });
        tag.setName(name);
        return tagRepository.save(tag);
    }

    @Transactional
    public void delete(Long id, User user) {
        Tag tag = getById(id, user);
        tagRepository.delete(tag);
    }
}
