package challenge.order.domain.exception;

public class DuplicatedOrderException extends Exception {

  public DuplicatedOrderException(String message) {
    super(message);
  }
}
