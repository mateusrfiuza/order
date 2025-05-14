package challenge.order.dataprovider.database.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import challenge.order.dataprovider.database.entity.OrderEntity;
import challenge.order.dataprovider.database.entity.OrderItemEntity;
import challenge.order.domain.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderEntityMapper {

  Order toDomain(OrderEntity entity);
  OrderEntity toEntity(Order domain);

  @AfterMapping
  default void setOrderReference(@MappingTarget OrderEntity entity) {
    if (entity.getItems() != null) {
      for (OrderItemEntity item : entity.getItems()) {
        item.setOrder(entity);
      }
    }
  }

}
