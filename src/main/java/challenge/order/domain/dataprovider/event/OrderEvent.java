package challenge.order.domain.dataprovider.event;

import challenge.order.domain.entity.Order;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract sealed class OrderEvent permits OrderCreatedEvent {

    private final Order order;

}
