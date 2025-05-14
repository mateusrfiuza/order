package challenge.order.entrypoint.http.payload;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(UUID id, UUID productId, Long quantity, BigDecimal price) {
}
