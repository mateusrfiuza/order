package challenge.order.domain.dataprovider.repository;

import java.util.List;

public interface Page<T> {
    List<T> getContent();
    int getPage();
    int getSize();
    long getTotalElements();
    int getTotalPages();
    boolean hasNext();
}
