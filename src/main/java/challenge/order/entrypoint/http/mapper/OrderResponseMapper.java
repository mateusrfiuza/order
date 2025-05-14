package challenge.order.entrypoint.http.mapper;

import org.mapstruct.Mapper;

import challenge.order.domain.entity.Order;
import challenge.order.entrypoint.http.payload.OrderResponse;

@Mapper(componentModel = "spring")
public interface OrderResponseMapper {

  OrderResponse toResponse(Order order);

}
