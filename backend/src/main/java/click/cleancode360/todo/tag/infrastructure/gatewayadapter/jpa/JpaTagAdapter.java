package click.cleancode360.todo.tag.infrastructure.gatewayadapter.jpa;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.shared.exception.domain.entity.ServletResponseException;
import click.cleancode360.todo.shared.log.infrastructure.gatewayadapter.jpa.JpaQueryLogger;
import click.cleancode360.todo.shared.pagination.domain.entity.PageRequest;
import click.cleancode360.todo.shared.pagination.domain.entity.PageResult;
import click.cleancode360.todo.shared.pagination.infrastructure.gatewayadapter.spring.SpringPageMapper;
import click.cleancode360.todo.tag.domain.entity.Tag;
import click.cleancode360.todo.tag.domain.gateway.TagGateway;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaTagAdapter implements TagGateway {

    private final JpaTagRepository jpaRepository;
    private final JpaQueryLogger queryLogger;

    @Override
    public PageResult<Tag> getAll(User user, PageRequest pageRequest) {
        return queryLogger.queryAndLog("findAllByUser", () ->
            SpringPageMapper.toPageResult(
                jpaRepository.findAllByUser(user, SpringPageMapper.toPageable(pageRequest))));
    }

    @Override
    public Tag getById(UUID id, User user) {
        return queryLogger.queryAndLog("findByIdAndUser", () -> jpaRepository.findByIdAndUser(id, user))
            .orElseThrow(() -> new ServletResponseException(404, "Tag not found: " + id));
    }

    @Override
    public Tag create(String name, User user) {
        findByNameIgnoreCaseAndUser(name, user).ifPresent(existing -> {
            throw new ServletResponseException(409, "Tag already exists: " + name);
        });
        Tag tag = new Tag(name);
        tag.setUser(user);
        return save(tag);
    }

    @Override
    public Tag update(UUID id, String name, User user) {
        Tag tag = getById(id, user);
        findByNameIgnoreCaseAndUser(name, user)
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new ServletResponseException(409, "Tag already exists: " + name);
            });
        tag.setName(name);
        return save(tag);
    }

    @Override
    public void delete(UUID id, User user) {
        Tag tag = getById(id, user);
        for (var task : tag.getTasks()) {
            task.removeTag(tag);
        }
        tag.getTasks().clear();
        queryLogger.queryAndLog("delete", () -> {
            jpaRepository.delete(tag);
            return null;
        });
    }

    @Override
    public Optional<Tag> findByIdAndUser(UUID id, User user) {
        return queryLogger.queryAndLog("findByIdAndUser", () -> jpaRepository.findByIdAndUser(id, user));
    }

    @Override
    public Optional<Tag> findByNameIgnoreCaseAndUser(String name, User user) {
        return queryLogger.queryAndLog("findByNameIgnoreCaseAndUser", () -> jpaRepository.findByNameIgnoreCaseAndUser(name, user));
    }

    @Override
    public Tag save(Tag tag) {
        return queryLogger.queryAndLog("save", () -> jpaRepository.save(tag));
    }
}
