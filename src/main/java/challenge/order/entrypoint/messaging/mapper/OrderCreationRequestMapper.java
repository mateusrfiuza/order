package challenge.order.entrypoint.messaging.mapper;

import org.mapstruct.Mapper;

import java.time.Instant;

import challenge.order.domain.usecase.commands.CreateOrderCommand;
import challenge.order.events.OrderCreationRequestSchema;

@Mapper(componentModel = "spring", imports = Instant.class)
public interface OrderCreationRequestMapper {

  CreateOrderCommand toCommand(OrderCreationRequestSchema source);

}
