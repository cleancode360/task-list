package click.cleancode360.todo.tag.domain.gateway;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.shared.pagination.domain.entity.PageRequest;
import click.cleancode360.todo.shared.pagination.domain.entity.PageResult;
import click.cleancode360.todo.tag.domain.entity.Tag;
import java.util.Optional;

public interface TagGateway {
    PageResult<Tag> getAll(User user, PageRequest pageRequest);
    Tag getById(Long id, User user);
    Tag create(String name, User user);
    Tag update(Long id, String name, User user);
    void delete(Long id, User user);
    Optional<Tag> findByIdAndUser(Long id, User user);
    Optional<Tag> findByNameIgnoreCaseAndUser(String name, User user);
    Tag save(Tag tag);
}
