package com.example.todo.application.service;

import com.example.todo.application.exception.NotFoundException;
import com.example.todo.domain.model.Tag;
import com.example.todo.infrastructure.repository.TagRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Tag getById(Long id) {
        return tagRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Tag not found: " + id));
    }

    @Transactional
    public Tag create(String name) {
        tagRepository.findByNameIgnoreCase(name).ifPresent(existing -> {
            throw new IllegalArgumentException("Tag already exists: " + name);
        });
        return tagRepository.save(new Tag(name));
    }

    @Transactional
    public Tag update(Long id, String name) {
        Tag tag = getById(id);
        tag.setName(name);
        return tagRepository.save(tag);
    }

    @Transactional
    public void delete(Long id) {
        Tag tag = getById(id);
        tagRepository.delete(tag);
    }
}
