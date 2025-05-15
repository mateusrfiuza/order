package challenge.order.domain.entity;


import java.math.BigDecimal;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = {"id", "productId"})
public class OrderItem {

  private final UUID id;
  private final UUID productId;
  private final Long quantity;
  private final BigDecimal price;

  public OrderItem(UUID id, UUID productId, Long quantity, BigDecimal price) {
    this.id = id;
    this.productId = DomainPreconditions.requireNonNull(productId,"productId must not be null");
    this.quantity = DomainPreconditions.requirePositive(quantity,"quantity must be greater than zero");
    this.price = DomainPreconditions.requirePositive(price,"price must be greater than zero");
  }

}
