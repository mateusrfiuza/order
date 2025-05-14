package challenge.order.dataprovider.database;

import java.util.List;

import challenge.order.domain.dataprovider.repository.Page;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageImpl<T> implements Page<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;

    @Override
    public int getTotalPages() {
        return size == 0
            ? 0
            : (int) Math.ceil((double) totalElements / size);
    }

    @Override
    public boolean hasNext() {
        return page + 1 < getTotalPages();
    }
}
