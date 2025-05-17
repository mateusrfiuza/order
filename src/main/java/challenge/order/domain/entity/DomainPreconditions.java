package challenge.order.domain.entity;

import java.math.BigDecimal;
import java.util.Collection;

public final class DomainPreconditions {
    private DomainPreconditions() {}

    public static <T> T requireNonNull(T obj, String message) {
      if (obj == null) {
        throw new IllegalArgumentException(message);
      }
        return obj;
    }

  public static <C extends Collection<?>> C requireNonEmpty(C coll, String message) {
    if (coll == null ||  coll.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
    return coll;
  }

    public static long requirePositive(Long value, String message) {
      if (value == null || value <= 0) {
        throw new IllegalArgumentException(message);
      }
        return value;
    }

    public static BigDecimal requirePositive(BigDecimal value, String message) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
