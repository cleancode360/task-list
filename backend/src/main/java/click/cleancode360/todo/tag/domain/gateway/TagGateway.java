package click.cleancode360.todo.tag.domain.gateway;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.shared.pagination.domain.entity.PageRequest;
import click.cleancode360.todo.shared.pagination.domain.entity.PageResult;
import click.cleancode360.todo.tag.domain.entity.Tag;
import java.util.Optional;
import java.util.UUID;

public interface TagGateway {
    PageResult<Tag> getAll(User user, PageRequest pageRequest);
    Tag getById(UUID id, User user);
    Tag create(String name, User user);
    Tag update(UUID id, String name, User user);
    void delete(UUID id, User user);
    Optional<Tag> findByIdAndUser(UUID id, User user);
    Optional<Tag> findByNameIgnoreCaseAndUser(String name, User user);
    Tag save(Tag tag);
}
