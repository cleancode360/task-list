package com.example.todo.tag.infrastructure.gatewayadapter.jpa;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.tag.domain.entity.Tag;
import com.example.todo.tag.domain.gateway.TagGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaTagGateway implements TagGateway {

    private final JpaTagRepository jpaRepository;

    @Override
    public List<Tag> findAllByUser(User user) {
        return jpaRepository.findAllByUser(user);
    }

    @Override
    public Optional<Tag> findByIdAndUser(Long id, User user) {
        return jpaRepository.findByIdAndUser(id, user);
    }

    @Override
    public Optional<Tag> findByNameIgnoreCaseAndUser(String name, User user) {
        return jpaRepository.findByNameIgnoreCaseAndUser(name, user);
    }

    @Override
    public Tag save(Tag tag) {
        return jpaRepository.save(tag);
    }

    @Override
    public void delete(Tag tag) {
        jpaRepository.delete(tag);
    }
}
