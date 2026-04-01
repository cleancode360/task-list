package click.cleancode360.todo.tag.domain.gateway;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.tag.domain.entity.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagGateway {
    List<Tag> findAllByUser(User user);
    Page<Tag> findAllByUser(User user, Pageable pageable);
    Optional<Tag> findByIdAndUser(Long id, User user);
    Optional<Tag> findByNameIgnoreCaseAndUser(String name, User user);
    Tag save(Tag tag);
    void delete(Tag tag);
}
