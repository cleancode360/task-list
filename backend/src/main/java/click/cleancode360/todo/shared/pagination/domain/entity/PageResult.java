package click.cleancode360.todo.shared.pagination.domain.entity;

import java.util.List;

public record PageResult<T>(List<T> content, int page, int size,
                            long totalElements, int totalPages) {}
