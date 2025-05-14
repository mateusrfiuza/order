package challenge.order.domain.usecase.commands;

import java.math.BigDecimal;
import java.util.UUID;

import challenge.order.domain.entity.OrderItem;

public record CreateOrderItemCommand(
    Long quantity,
    BigDecimal price,
    UUID productId
) {

  public static OrderItem toDomain(CreateOrderItemCommand command) {
    return new OrderItem(null, command.productId(), command.quantity(), command.price());
  }
}
