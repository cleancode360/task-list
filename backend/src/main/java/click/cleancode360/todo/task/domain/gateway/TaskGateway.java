package click.cleancode360.todo.task.domain.gateway;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.shared.pagination.domain.entity.PageRequest;
import click.cleancode360.todo.shared.pagination.domain.entity.PageResult;
import click.cleancode360.todo.task.domain.entity.Task;
import java.util.List;

public interface TaskGateway {
    PageResult<Task> getAll(User user, PageRequest pageRequest);
    Task getById(Long id, User user);
    Task create(String title, String description, List<String> tagNames, User user);
    Task update(Long id, String title, String description, Boolean completed,
                List<String> tagNames, User user);
    Task toggle(Long id, User user);
    void delete(Long id, User user);
    Task addTag(Long taskId, Long tagId, User user);
    Task removeTag(Long taskId, Long tagId, User user);
}
