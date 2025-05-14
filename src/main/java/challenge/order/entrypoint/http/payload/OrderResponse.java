package challenge.order.entrypoint.http.payload;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import challenge.order.domain.entity.OrderStatus;

public record OrderResponse(
    UUID id,
    UUID customerId,
    UUID sellerId,
    Set<OrderItemResponse> items,
    BigDecimal totalPrice,
    OrderStatus status,
    Instant createdAt
) {

}
