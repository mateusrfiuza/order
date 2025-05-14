package challenge.order.domain.usecase.commands;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import challenge.order.domain.entity.Order;
import challenge.order.domain.entity.OrderStatus;

public record CreateOrderCommand(
        UUID sellerId,
        UUID customerId,
        Set<CreateOrderItemCommand> items
) {

  public static Order toDomain(CreateOrderCommand command) {
    var orderItems = command.items().stream()
        .map(CreateOrderItemCommand::toDomain)
        .collect(Collectors.toSet());

    return new Order(
        null,
        command.customerId(),
        command.sellerId(),
        orderItems,
        null,
        OrderStatus.CREATED,
        null
    );

  }

}


