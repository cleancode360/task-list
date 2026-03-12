package com.example.todo.tag.domain.gateway;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.tag.domain.entity.Tag;
import java.util.List;
import java.util.Optional;

public interface TagGateway {
    List<Tag> findAllByUser(User user);
    Optional<Tag> findByIdAndUser(Long id, User user);
    Optional<Tag> findByNameIgnoreCaseAndUser(String name, User user);
    Tag save(Tag tag);
    void delete(Tag tag);
}
