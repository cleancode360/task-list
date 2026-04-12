package click.cleancode360.todo.tag.infrastructure.gatewayadapter.jpa;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.tag.domain.entity.Tag;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaTagRepository extends JpaRepository<Tag, UUID> {
    List<Tag> findAllByUser(User user);
    Page<Tag> findAllByUser(User user, Pageable pageable);
    Optional<Tag> findByIdAndUser(UUID id, User user);
    Optional<Tag> findByNameIgnoreCaseAndUser(String name, User user);
}
