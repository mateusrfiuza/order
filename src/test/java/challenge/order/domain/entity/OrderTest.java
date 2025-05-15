package challenge.order.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.Assertions.*;

class OrderTest {

  private UUID id;
  private UUID customerId;
  private UUID sellerId;
  private Instant createdAt;
  private OrderItem item1;
  private OrderItem item2;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    customerId = UUID.randomUUID();
    sellerId = UUID.randomUUID();
    createdAt = Instant.now();
    item1 = new OrderItem(null, UUID.randomUUID(), 2L, BigDecimal.valueOf(10));
    item2 = new OrderItem(null, UUID.randomUUID(), 3L, BigDecimal.valueOf(5));
  }

  @Test
  void constructor_nullStatus_throwsIllegalArgumentException() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new Order(
            id, customerId, sellerId,
            Set.of(item1),
            BigDecimal.ZERO,
            null,
            createdAt
        ))
        .withMessage("status must not be null");
  }

  @Test
  void constructor_nullCustomerId_throwsIllegalArgumentException() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new Order(
            id, null, sellerId,
            Set.of(item1),
            BigDecimal.ZERO,
            OrderStatus.CREATED,
            createdAt
        ))
        .withMessage("customerId must not be null");
  }

  @Test
  void constructor_nullSellerId_throwsIllegalArgumentException() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new Order(
            id, customerId, null,
            Set.of(item1),
            BigDecimal.ZERO,
            OrderStatus.CREATED,
            createdAt
        ))
        .withMessage("sellerId must not be null");
  }

  @Test
  void constructor_emptyItems_throwsIllegalArgumentException() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new Order(
            id, customerId, sellerId,
            Set.of(),
            BigDecimal.ZERO,
            OrderStatus.CREATED,
            createdAt
        ))
        .withMessage("items must not be empty");
  }

  @Test
  void constructor_withTotalPrice_usesProvidedTotal() {

    var provided = BigDecimal.valueOf(123.45);
    var order = new Order(
        id, customerId, sellerId,
        Set.of(item1, item2),
        provided,
        OrderStatus.CREATED,
        createdAt
    );
    assertThat(order.getTotalPrice()).isEqualByComparingTo(provided);
  }

  @Test
  void constructor_withoutTotalPrice_recalculatesTotal() {
    var order = new Order(
        id, customerId, sellerId,
        Set.of(item1, item2),
        null,
        OrderStatus.CREATED,
        createdAt
    );
    assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(35));
  }

  @Test
  void addItem_increasesItemsAndRecalculatesTotal() {
    var order = new Order(
        id, customerId, sellerId,
        Set.of(item1),
        null,
        OrderStatus.CREATED,
        createdAt
    );
    var newItem = new OrderItem(null, UUID.randomUUID(), 80L, BigDecimal.valueOf(12.33));

    order.addItem(newItem);

    assertThat(order.getItems()).containsExactlyInAnyOrder(item1, newItem);
    assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(1006.40));
  }

  @Test
  void removeItem_removesAndRecalculatesTotal() {
    var order = new Order(
        id, customerId, sellerId,
        Set.of(item1, item2),
        null,
        OrderStatus.CREATED,
        createdAt
    );

    order.removeItem(item1);

    assertThat(order.getItems()).containsExactly(item2);
    assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(15));
  }

  @Test
  void removeItem_nonExisting_doesNothing() {
    var order = new Order(
        id, customerId, sellerId,
        Set.of(item1),
        null,
        OrderStatus.CREATED,
        createdAt
    );
    var other = new OrderItem(null, UUID.randomUUID(), 2L, BigDecimal.valueOf(10));

    order.removeItem(other);

    assertThat(order.getItems()).containsExactly(item1);
    assertThat(order.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(20));
  }

  @Test
  void setStatus_updatesStatus() {
    var order = new Order(
        id, customerId, sellerId,
        Set.of(item1),
        null,
        OrderStatus.CREATED,
        createdAt
    );

    order.setStatus(OrderStatus.COMPLETED);

    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
  }

  @Test
  void setStatus_null_throwsIllegalArgumentException() {
    var order = new Order(
        id, customerId, sellerId,
        Set.of(item1),
        null,
        OrderStatus.CREATED,
        createdAt
    );

    assertThatIllegalArgumentException()
        .isThrownBy(() -> order.setStatus(null))
        .withMessage("status must not be null");
  }

  @Test
  void getItems_returnsUnmodifiableSet() {
    var order = new Order(
        id, customerId, sellerId,
        Set.of(item1),
        null,
        OrderStatus.CREATED,
        createdAt
    );
    var items = order.getItems();
    assertThatThrownBy(() -> items.remove(item1))
        .isInstanceOf(UnsupportedOperationException.class);
  }
}

