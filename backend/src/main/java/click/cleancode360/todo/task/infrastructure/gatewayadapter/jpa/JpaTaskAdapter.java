package click.cleancode360.todo.task.infrastructure.gatewayadapter.jpa;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.shared.exception.domain.entity.ServletResponseException;
import click.cleancode360.todo.shared.log.infrastructure.gatewayadapter.jpa.JpaQueryLogger;
import click.cleancode360.todo.shared.pagination.domain.entity.PageRequest;
import click.cleancode360.todo.shared.pagination.domain.entity.PageResult;
import click.cleancode360.todo.shared.pagination.infrastructure.gatewayadapter.spring.SpringPageMapper;
import click.cleancode360.todo.tag.domain.entity.Tag;
import click.cleancode360.todo.tag.domain.gateway.TagGateway;
import click.cleancode360.todo.task.domain.entity.Task;
import click.cleancode360.todo.task.domain.gateway.TaskGateway;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaTaskAdapter implements TaskGateway {

    private final JpaTaskRepository jpaRepository;
    private final JpaQueryLogger queryLogger;
    private final TagGateway tagGateway;

    @Override
    public PageResult<Task> getAll(User user, PageRequest pageRequest) {
        return queryLogger.queryAndLog("findAllByUser", () ->
            SpringPageMapper.toPageResult(
                jpaRepository.findAllByUser(user, SpringPageMapper.toPageable(pageRequest))));
    }

    @Override
    public Task getById(UUID id, User user) {
        return queryLogger.queryAndLog("findByIdAndUser", () -> jpaRepository.findByIdAndUser(id, user))
            .orElseThrow(() -> new ServletResponseException(404, "Task not found: " + id));
    }

    @Override
    public Task create(String title, String description, List<String> tagNames, User user) {
        Task task = new Task(title, description);
        task.setUser(user);
        for (Tag tag : resolveTagsByName(tagNames, user)) {
            task.addTag(tag);
        }
        return queryLogger.queryAndLog("save", () -> jpaRepository.save(task));
    }

    @Override
    public Task update(UUID id, String title, String description, Boolean completed,
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
        return queryLogger.queryAndLog("save", () -> jpaRepository.save(task));
    }

    @Override
    public Task toggle(UUID id, User user) {
        Task task = getById(id, user);
        task.setCompleted(!task.isCompleted());
        return queryLogger.queryAndLog("save", () -> jpaRepository.save(task));
    }

    @Override
    public void delete(UUID id, User user) {
        Task task = getById(id, user);
        queryLogger.queryAndLog("delete", () -> {
            jpaRepository.delete(task);
            return null;
        });
    }

    @Override
    public Task addTag(UUID taskId, UUID tagId, User user) {
        Task task = getById(taskId, user);
        Tag tag = tagGateway.findByIdAndUser(tagId, user)
            .orElseThrow(() -> new ServletResponseException(404, "Tag not found: " + tagId));
        task.addTag(tag);
        return queryLogger.queryAndLog("save", () -> jpaRepository.save(task));
    }

    @Override
    public Task removeTag(UUID taskId, UUID tagId, User user) {
        Task task = getById(taskId, user);
        Tag tag = tagGateway.findByIdAndUser(tagId, user)
            .orElseThrow(() -> new ServletResponseException(404, "Tag not found: " + tagId));
        task.removeTag(tag);
        return queryLogger.queryAndLog("save", () -> jpaRepository.save(task));
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
}
