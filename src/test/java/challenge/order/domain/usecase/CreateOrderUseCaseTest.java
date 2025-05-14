package challenge.order.domain.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import challenge.order.config.CreateOrderProperties;
import challenge.order.domain.dataprovider.event.OrderCreatedEvent;
import challenge.order.domain.dataprovider.event.OrderNotifier;
import challenge.order.domain.dataprovider.repository.OrderRepository;
import challenge.order.domain.entity.Order;
import challenge.order.domain.exception.DuplicatedOrderException;
import challenge.order.domain.service.CacheService;
import challenge.order.domain.usecase.commands.CreateOrderCommand;
import challenge.order.domain.usecase.commands.CreateOrderItemCommand;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private OrderNotifier orderNotifier;

  @Mock
  private CacheService cacheService;

  @Mock
  private CreateOrderProperties properties;

  @InjectMocks
  private CreateOrderUseCase useCase;

  @Captor
  private ArgumentCaptor<OrderCreatedEvent> eventCaptor;

  private static final Duration DEFAULT_TTL = Duration.ofHours(2);
  private static final String DEFAULT_KEY_PATTERN = "create-order:%s-%s";

  @Test
  void execute_nullCommand_throwsNullPointerException() {
    var ex = assertThrows(
        NullPointerException.class,
        () -> useCase.execute(null)
    );
    assertEquals("command cannot be null.", ex.getMessage());
  }

  @Test
  void execute_success() throws DuplicatedOrderException {
    stubProperties();
    when(cacheService.putIfAbsent(any(), any(), any())).thenReturn(true);
    Order saved = mock(Order.class);
    when(orderRepository.saveOrder(any(Order.class))).thenReturn(saved);

    var command = generateNewCommand();
    useCase.execute(command);

    var expectedKey = buildDeduplicationKey(command);
    verify(cacheService).putIfAbsent(eq(expectedKey), anyString(), eq(DEFAULT_TTL));
    verify(orderRepository).saveOrder(CreateOrderCommand.toDomain(command));
    verify(orderNotifier).notify(eventCaptor.capture());
    assertSame(saved, eventCaptor.getValue().getOrder());
  }

  @Test
  void execute_duplicate_throwsDuplicatedOrderException() {
    stubProperties();
    when(cacheService.putIfAbsent(any(), any(), any())).thenReturn(false);

    assertThrows(DuplicatedOrderException.class, () -> useCase.execute(generateNewCommand()));

    verify(orderRepository, never()).saveOrder(any());
    verify(orderNotifier, never()).notify(any());
  }

  @Test
  void execute_saveError_evictsCacheAndRethrows() {
    stubProperties();
    when(cacheService.putIfAbsent(any(), any(), any())).thenReturn(true);
    RuntimeException error = new RuntimeException();
    when(orderRepository.saveOrder(any(Order.class))).thenThrow(error);

    var command = generateNewCommand();
    assertThrows(
        RuntimeException.class,
        () -> useCase.execute(command)
    );

    var expectedKey = buildDeduplicationKey(command);
    verify(cacheService).evict(expectedKey);
    verify(orderNotifier, never()).notify(any());
  }

  public CreateOrderCommand generateNewCommand() {
    return new CreateOrderCommand(
        UUID.randomUUID(),
        UUID.randomUUID(),
        Set.of(
            new CreateOrderItemCommand(1L, new BigDecimal("10.00"), UUID.randomUUID()))
    );
  }

  private void stubProperties() {
    when(properties.getDeduplicationKeyPattern()).thenReturn(DEFAULT_KEY_PATTERN);
    when(properties.getDeduplicationTtl()).thenReturn(DEFAULT_TTL);
  }

  public String buildDeduplicationKey(CreateOrderCommand command) {
    return String.format(DEFAULT_KEY_PATTERN, command.customerId(), command.hashCode());
  }

}
