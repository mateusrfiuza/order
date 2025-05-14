package challenge.order.dataprovider.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import challenge.order.domain.dataprovider.event.OrderCreatedEvent;
import challenge.order.domain.entity.OrderItem;
import challenge.order.events.Item;
import challenge.order.events.OrderCreatedSchema;

@Mapper(
    componentModel = "spring"
)
public interface OrderCreatedSchemaMapper {

  @Mapping(target = "orderId", source = "order.id")
  @Mapping(target = "customerId", source = "order.customerId")
  @Mapping(target = "sellerId", source = "order.sellerId")
  @Mapping(target = "totalPrice", source = "order.totalPrice")
  @Mapping(target = "createdAt", source = "order.createdAt", qualifiedByName = "instantToString")
  @Mapping(target = "items", source = "order.items", qualifiedByName = "mapItems")
  OrderCreatedSchema toAvro(OrderCreatedEvent event);

  @Named("instantToString")
  static String instantToString(Instant instant) {
    return instant != null ? instant.toString() : null;
  }

  @Named("mapItems")
  static List<Item> mapItems(Set<OrderItem> items) {
    return items.stream()
        .map(item -> Item.newBuilder()
            .setProductId(item.getProductId())
            .setQuantity(item.getQuantity())
            .setPrice(item.getPrice())
            .build()
        ).collect(Collectors.toList());
  }
}
