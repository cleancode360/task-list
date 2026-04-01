package click.cleancode360.todo.tag.application.usecase;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.shared.exception.domain.entity.ServletResponseException;
import click.cleancode360.todo.tag.domain.entity.Tag;
import click.cleancode360.todo.tag.domain.gateway.TagGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class TagUseCase {

    private final TagGateway tagGateway;

    public List<Tag> getAll(User user) {
        return tagGateway.findAllByUser(user);
    }

    public Page<Tag> getAll(User user, Pageable pageable) {
        return tagGateway.findAllByUser(user, pageable);
    }

    public Tag getById(Long id, User user) {
        return tagGateway.findByIdAndUser(id, user)
            .orElseThrow(() -> new ServletResponseException(404, "Tag not found: " + id));
    }

    public Tag create(String name, User user) {
        tagGateway.findByNameIgnoreCaseAndUser(name, user).ifPresent(existing -> {
            throw new ServletResponseException(409, "Tag already exists: " + name);
        });
        Tag tag = new Tag(name);
        tag.setUser(user);
        return tagGateway.save(tag);
    }

    public Tag update(Long id, String name, User user) {
        Tag tag = getById(id, user);
        tagGateway.findByNameIgnoreCaseAndUser(name, user)
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new ServletResponseException(409, "Tag already exists: " + name);
            });
        tag.setName(name);
        return tagGateway.save(tag);
    }

    @Transactional
    public void delete(Long id, User user) {
        Tag tag = getById(id, user);
        for (var task : tag.getTasks()) {
            task.removeTag(tag);
        }
        tag.getTasks().clear();
        tagGateway.delete(tag);
    }
}
