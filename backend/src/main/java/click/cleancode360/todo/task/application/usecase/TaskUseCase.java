package click.cleancode360.todo.task.application.usecase;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.shared.exception.domain.entity.ServletResponseException;
import click.cleancode360.todo.tag.domain.entity.Tag;
import click.cleancode360.todo.tag.domain.gateway.TagGateway;
import click.cleancode360.todo.task.domain.entity.Task;
import click.cleancode360.todo.task.domain.gateway.TaskGateway;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class TaskUseCase {

    private final TaskGateway taskGateway;
    private final TagGateway tagGateway;

    public List<Task> getAll(User user) {
        return taskGateway.findAllByUser(user);
    }

    public Page<Task> getAll(User user, Pageable pageable) {
        return taskGateway.findAllByUser(user, pageable);
    }

    public Task getById(Long id, User user) {
        return taskGateway.findByIdAndUser(id, user)
            .orElseThrow(() -> new ServletResponseException(404, "Task not found: " + id));
    }

    public Task create(String title, String description, List<String> tagNames, User user) {
        Task task = new Task(title, description);
        task.setUser(user);
        for (Tag tag : resolveTagsByName(tagNames, user)) {
            task.addTag(tag);
        }
        return taskGateway.save(task);
    }

    public Task update(Long id, String title, String description, Boolean completed,
                       List<String> tagNames, User user) {
        Task task = getById(id, user);
        if (title != null) {
            task.setTitle(title);
        }
        if (description != null) {
            task.setDescription(description);
        }
        if (completed != null) {
            task.setCompleted(completed);
        }
        if (tagNames != null) {
            task.getTags().clear();
            for (Tag tag : resolveTagsByName(tagNames, user)) {
                task.addTag(tag);
            }
        }
        return taskGateway.save(task);
    }

    private List<Tag> resolveTagsByName(List<String> tagNames, User user) {
        List<Tag> tags = new ArrayList<>();
        if (tagNames != null) {
            for (String name : tagNames) {
                Tag tag = tagGateway.findByNameIgnoreCaseAndUser(name, user)
                    .orElseGet(() -> {
                        Tag newTag = new Tag(name);
                        newTag.setUser(user);
                        return tagGateway.save(newTag);
                    });
                tags.add(tag);
            }
        }
        return tags;
    }

    public Task toggle(Long id, User user) {
        Task task = getById(id, user);
        task.setCompleted(!task.isCompleted());
        return taskGateway.save(task);
    }

    public void delete(Long id, User user) {
        Task task = getById(id, user);
        taskGateway.delete(task);
    }

    public Task addTag(Long taskId, Long tagId, User user) {
        Task task = getById(taskId, user);
        Tag tag = tagGateway.findByIdAndUser(tagId, user)
            .orElseThrow(() -> new ServletResponseException(404, "Tag not found: " + tagId));
        task.addTag(tag);
        return taskGateway.save(task);
    }

    public Task removeTag(Long taskId, Long tagId, User user) {
        Task task = getById(taskId, user);
        Tag tag = tagGateway.findByIdAndUser(tagId, user)
            .orElseThrow(() -> new ServletResponseException(404, "Tag not found: " + tagId));
        task.removeTag(tag);
        return taskGateway.save(task);
    }
}
