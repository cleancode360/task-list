package com.example.todo.infrastructure.repository;

import com.example.todo.domain.model.Tag;
import com.example.todo.domain.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAllByUser(User user);

    Optional<Tag> findByIdAndUser(Long id, User user);

    Optional<Tag> findByNameIgnoreCaseAndUser(String name, User user);
}
