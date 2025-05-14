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
        if (productId == null) {
            throw new IllegalArgumentException("productId must not be null");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("item quantity must be greater than zero");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("price must be greater than zero");
        }

        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

}
