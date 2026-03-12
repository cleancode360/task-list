package com.example.todo.tag.infrastructure.gatewayadapter;

import com.example.todo.auth.domain.entity.User;
import com.example.todo.tag.domain.entity.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface TagJpaRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByUser(User user);
    Optional<Tag> findByIdAndUser(Long id, User user);
    Optional<Tag> findByNameIgnoreCaseAndUser(String name, User user);
}
