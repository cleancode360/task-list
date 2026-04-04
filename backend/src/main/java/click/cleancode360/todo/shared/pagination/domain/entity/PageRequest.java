package click.cleancode360.todo.shared.pagination.domain.entity;

import java.util.List;

public record PageRequest(int page, int size, List<SortField> sort) {

    public PageRequest(int page, int size) {
        this(page, size, List.of());
    }

    public record SortField(String property, Direction direction) {}

    public enum Direction { ASC, DESC }
}
