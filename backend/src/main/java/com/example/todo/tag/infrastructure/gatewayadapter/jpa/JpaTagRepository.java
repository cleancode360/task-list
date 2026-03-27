package com.example.todo.tag.infrastructure.gatewayadapter.jpa;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.tag.domain.entity.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaTagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByUser(User user);
    Page<Tag> findAllByUser(User user, Pageable pageable);
    Optional<Tag> findByIdAndUser(Long id, User user);
    Optional<Tag> findByNameIgnoreCaseAndUser(String name, User user);
}
