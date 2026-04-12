package click.cleancode360.todo.task.domain.gateway;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.shared.pagination.domain.entity.PageRequest;
import click.cleancode360.todo.shared.pagination.domain.entity.PageResult;
import click.cleancode360.todo.task.domain.entity.Task;
import java.util.List;
import java.util.UUID;

public interface TaskGateway {
    PageResult<Task> getAll(User user, PageRequest pageRequest);
    Task getById(UUID id, User user);
    Task create(String title, String description, List<String> tagNames, User user);
    Task update(UUID id, String title, String description, Boolean completed,
                List<String> tagNames, User user);
    Task toggle(UUID id, User user);
    void delete(UUID id, User user);
    Task addTag(UUID taskId, UUID tagId, User user);
    Task removeTag(UUID taskId, UUID tagId, User user);
}
