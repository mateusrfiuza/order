package challenge.order.domain.dataprovider.repository;

import java.util.Optional;
import java.util.UUID;

import challenge.order.domain.entity.Order;

public interface OrderRepository {

    Order saveOrder(Order order);
    Optional<Order> getOrderById(UUID orderId);
    Page<Order> findWithCriteria(OrderSearchCriteria searchCriteria, PaginationRequest pageRequest);

}
