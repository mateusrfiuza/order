package challenge.order.entrypoint.http.payload;

import java.util.List;

public record PageResponse<T>(
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    List<T> content
) {}
