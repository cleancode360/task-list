package click.cleancode360.todo.task.infrastructure.gatewayadapter.jpa;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.task.domain.entity.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface JpaTaskRepository extends JpaRepository<Task, UUID> {
    @EntityGraph(attributePaths = "tags")
    List<Task> findAllByUser(User user);

    @EntityGraph(attributePaths = "tags")
    Page<Task> findAllByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = "tags")
    Optional<Task> findByIdAndUser(UUID id, User user);
}
