package click.cleancode360.todo.tag.infrastructure.gatewayadapter.jpa;

import click.cleancode360.todo.auth.domain.entity.User;
import click.cleancode360.todo.shared.log.infrastructure.gatewayadapter.jpa.JpaQueryLogger;
import click.cleancode360.todo.tag.domain.entity.Tag;
import click.cleancode360.todo.tag.domain.gateway.TagGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaTagAdapter implements TagGateway {

    private final JpaTagRepository jpaRepository;
    private final JpaQueryLogger queryLogger;

    @Override
    public List<Tag> findAllByUser(User user) {
        return queryLogger.queryAndLog("findAllByUser", () -> jpaRepository.findAllByUser(user));
    }

    @Override
    public Page<Tag> findAllByUser(User user, Pageable pageable) {
        return queryLogger.queryAndLog("findAllByUser(pageable)", () -> jpaRepository.findAllByUser(user, pageable));
    }

    @Override
    public Optional<Tag> findByIdAndUser(Long id, User user) {
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

    @Override
    public void delete(Tag tag) {
        queryLogger.queryAndLog("delete", () -> {
            jpaRepository.delete(tag);
            return null;
        });
    }
}
