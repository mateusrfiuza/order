package challenge.order.domain.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Order {

  private final UUID id;
  private final UUID customerId;
  private final UUID sellerId;
  private final Set<OrderItem> items;
  private OrderStatus status;
  private final Instant createdAt;

  private transient BigDecimal cachedTotal;
  private transient boolean dirty = true;

  public Order(UUID id,
               UUID customerId,
               UUID sellerId,
               Set<OrderItem> items,
               OrderStatus status,
               Instant createdAt) {
    this.id = id;
    this.createdAt = createdAt;
    this.customerId = DomainPreconditions.requireNonNull(customerId, "customerId must not be null");
    this.sellerId = DomainPreconditions.requireNonNull(sellerId, "sellerId must not be null");
    this.status = DomainPreconditions.requireNonNull(status, "status must not be null");
    this.items = Collections.synchronizedSet(new HashSet<>(DomainPreconditions.requireNonEmpty(items, "items must not be empty")));
  }

  public void addItem(OrderItem item) {
    DomainPreconditions.requireNonNull(item, "item must not be null");
    items.add(item);
    dirty = true;
  }

  public void removeItem(OrderItem item) {
    DomainPreconditions.requireNonNull(item, "item must not be null");
    if (!items.remove(item)) {
      throw new IllegalArgumentException("item not found in the order");
    }
    DomainPreconditions.requireNonEmpty(items, "items must not be empty");
    dirty = true;
  }

  public Set<OrderItem> getItems() {
    return Collections.unmodifiableSet(items);
  }

  public void setStatus(OrderStatus newStatus) {
    DomainPreconditions.requireNonNull(newStatus, "status must not be null");
    this.status = newStatus;
  }

  public BigDecimal getTotalPrice() {
    if (dirty) {
      cachedTotal = calculateTotalPrice();
      dirty = false;
    }
    return cachedTotal;
  }

  private BigDecimal calculateTotalPrice() {
    return items.stream()
        .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

}

