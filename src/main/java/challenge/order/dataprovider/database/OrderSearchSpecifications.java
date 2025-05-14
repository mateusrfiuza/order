package challenge.order.dataprovider.database;

import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import challenge.order.dataprovider.database.entity.OrderEntity;
import challenge.order.domain.dataprovider.repository.OrderByDirection;

public final class OrderSearchSpecifications {

  private OrderSearchSpecifications() {}

  public static Specification<OrderEntity> hasCustomerId(UUID customerId) {
    return (root, query, cb) ->
        customerId != null ? cb.equal(root.get("customerId"), customerId) : cb.conjunction();
  }

  public static Specification<OrderEntity> hasSellerId(UUID sellerId) {
    return (root, query, cb) ->
        sellerId != null ? cb.equal(root.get("sellerId"), sellerId) : cb.conjunction();
  }

  public static Specification<OrderEntity> orderByCreatedAt(OrderByDirection direction) {
    return (root, query, cb) -> {
      var dir = direction == null ? OrderByDirection.DESC : direction;

      if (query.getOrderList().isEmpty()) {
        query.orderBy(
            dir == OrderByDirection.ASC
            ? cb.asc(root.get("createdAt"))
            : cb.desc(root.get("createdAt")));
      }
      return cb.conjunction();
    };
  }

}
