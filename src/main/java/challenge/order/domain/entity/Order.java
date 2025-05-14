package challenge.order.domain.entity;

import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Order {

  private final UUID id;
  private final UUID customerId;
  private final UUID sellerId;
  private BigDecimal totalPrice;
  private final Set<OrderItem> items = Collections.synchronizedSet(new HashSet<>());
  private OrderStatus status;
  private final Instant createdAt;

  public Order(UUID id,
               UUID customerId,
               UUID sellerId,
               Set<OrderItem> items,
               BigDecimal totalPrice,
               OrderStatus status,
               Instant createdAt) {
    this.id = id;
    this.createdAt = createdAt;
    this.status = Objects.requireNonNull(status, "status cannot be null");
    this.customerId = Objects.requireNonNull(customerId, "customerId cannot be null");
    this.sellerId = Objects.requireNonNull(sellerId, "sellerId cannot be null");

    if (CollectionUtils.isEmpty(items)) {
      throw new IllegalArgumentException("Items cannot be empty");
    }

    this.items.addAll(items);
    this.totalPrice = (totalPrice != null)
                      ? totalPrice
                      : recalculateTotal();
  }

  public void addItem(OrderItem item) {
    Objects.requireNonNull(item, "orderItem cannot be null");
    items.add(item);
    this.totalPrice = recalculateTotal();
  }

  public void removeItem(OrderItem item) {
    if (items.remove(item)) {
      this.totalPrice = recalculateTotal();
    }
  }

  public void setStatus(OrderStatus newStatus) {
    this.status = Objects.requireNonNull(newStatus, "status cannot be null");
  }

  public Set<OrderItem> getItems() {
    return items.stream()
        .collect(Collectors.toUnmodifiableSet());
  }

  private BigDecimal recalculateTotal() {
    return this.totalPrice = items.stream()
        .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

}

