package challenge.order.domain.entity;

import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
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

    this.customerId = DomainPreconditions.requireNonNull(customerId, "customerId must not be null");
    this.sellerId = DomainPreconditions.requireNonNull(sellerId, "sellerId must not be null");
    this.status = DomainPreconditions.requireNonNull(status, "status must not be null");
    DomainPreconditions.requireNonEmpty(items, "items must not be empty");
    this.items.addAll(items);

    this.totalPrice = (totalPrice != null)
                      ? totalPrice
                      : recalculateTotal();
  }

  public void addItem(OrderItem item) {
    DomainPreconditions.requireNonNull(item, "item must not be null");
    items.add(item);
    this.totalPrice = recalculateTotal();
  }

  public void removeItem(OrderItem item) {
    if (items.remove(item)) {
      this.totalPrice = recalculateTotal();
    }
  }

  public void setStatus(OrderStatus newStatus) {
    DomainPreconditions.requireNonNull(newStatus,"status must not be null");
    this.status = newStatus;
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

