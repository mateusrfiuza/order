package challenge.order.entrypoint.http.mapper;

import java.util.function.Function;

import challenge.order.domain.dataprovider.repository.Page;
import challenge.order.entrypoint.http.payload.PageResponse;

public final class PageResponseMapper {

  private PageResponseMapper() {}

  public static <I, O> PageResponse<O> toPageResponse(Page<I> page, Function<I, O> mapper) {
    var content = page.getContent().stream().map(mapper).toList();
    return new PageResponse<>(
        page.getPage(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.hasNext(),
        content
    );
  }

}
