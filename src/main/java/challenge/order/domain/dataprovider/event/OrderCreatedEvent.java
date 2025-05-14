package challenge.order.domain.dataprovider.event;

import challenge.order.domain.entity.Order;

public final class OrderCreatedEvent extends OrderEvent {

    public OrderCreatedEvent(final Order order) {
        super(order);
    }


}
