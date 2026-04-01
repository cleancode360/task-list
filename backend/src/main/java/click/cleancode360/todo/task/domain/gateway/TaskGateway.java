package click.cleancode360.todo.task.domain.gateway;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.task.domain.entity.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskGateway {
    List<Task> findAllByUser(User user);
    Page<Task> findAllByUser(User user, Pageable pageable);
    Optional<Task> findByIdAndUser(Long id, User user);
    Task save(Task task);
    void delete(Task task);
}
