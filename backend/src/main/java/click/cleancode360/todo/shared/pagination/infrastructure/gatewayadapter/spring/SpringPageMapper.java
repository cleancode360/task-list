package click.cleancode360.todo.shared.pagination.infrastructure.gatewayadapter.spring;

import click.cleancode360.todo.shared.pagination.domain.entity.PageRequest;
import click.cleancode360.todo.shared.pagination.domain.entity.PageResult;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpringPageMapper {

    public static Pageable toPageable(PageRequest request) {
        if (request.sort().isEmpty()) {
            return org.springframework.data.domain.PageRequest.of(request.page(), request.size());
        }
        List<Sort.Order> orders = request.sort().stream()
            .map(sf -> new Sort.Order(
                sf.direction() == PageRequest.Direction.ASC ? Sort.Direction.ASC : Sort.Direction.DESC,
                sf.property()))
            .toList();
        return org.springframework.data.domain.PageRequest.of(request.page(), request.size(), Sort.by(orders));
    }

    public static PageRequest toPageRequest(Pageable pageable) {
        List<PageRequest.SortField> sort = pageable.getSort().stream()
            .map(order -> new PageRequest.SortField(
                order.getProperty(),
                order.isAscending() ? PageRequest.Direction.ASC : PageRequest.Direction.DESC))
            .toList();
        return new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    public static <T> PageResult<T> toPageResult(Page<T> page) {
        return new PageResult<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages());
    }

    public static <T> Page<T> toPage(PageResult<T> result, Pageable pageable) {
        return new PageImpl<>(result.content(), pageable, result.totalElements());
    }
}
