package challenge.order.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

  private UUID id;
  private UUID productId;
  private Long quantity;
  private BigDecimal price;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    productId = UUID.randomUUID();
    quantity = 2L;
    price = BigDecimal.valueOf(10);
  }

  @Test
  void constructor_nullProductId_throwsIllegalArgumentException() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new OrderItem(id, null, quantity, price))
        .withMessage("productId must not be null");
  }

  @Test
  void constructor_nonPositiveQuantity_throwsIllegalArgumentException() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new OrderItem(id, productId, null, price))
        .withMessage("quantity must be greater than zero");

    assertThatIllegalArgumentException()
        .isThrownBy(() -> new OrderItem(id, productId, 0L, price))
        .withMessage("quantity must be greater than zero");

    assertThatIllegalArgumentException()
        .isThrownBy(() -> new OrderItem(id, productId, -1L, price))
        .withMessage("quantity must be greater than zero");
  }

  @Test
  void constructor_nonPositivePrice_throwsIllegalArgumentException() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new OrderItem(id, productId, quantity, null))
        .withMessage("price must be greater than zero");

    assertThatIllegalArgumentException()
        .isThrownBy(() -> new OrderItem(id, productId, quantity, BigDecimal.ZERO))
        .withMessage("price must be greater than zero");

    assertThatIllegalArgumentException()
        .isThrownBy(() -> new OrderItem(id, productId, quantity, BigDecimal.valueOf(-1)))
        .withMessage("price must be greater than zero");
  }

  @Test
  void constructor_validArguments_createsOrderItemSuccessfully() {
    var item = new OrderItem(id, productId, quantity, price);

    assertThat(item.getId()).isEqualTo(id);
    assertThat(item.getProductId()).isEqualTo(productId);
    assertThat(item.getQuantity()).isEqualTo(quantity);
    assertThat(item.getPrice()).isEqualTo(price);
  }

}
