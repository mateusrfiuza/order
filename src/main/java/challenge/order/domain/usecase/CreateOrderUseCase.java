package challenge.order.domain.usecase;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import challenge.order.config.CreateOrderProperties;
import challenge.order.domain.dataprovider.event.OrderCreatedEvent;
import challenge.order.domain.dataprovider.event.OrderNotifier;
import challenge.order.domain.dataprovider.repository.OrderRepository;
import challenge.order.domain.entity.Order;
import challenge.order.domain.exception.DuplicatedOrderException;
import challenge.order.domain.service.CacheService;
import challenge.order.domain.usecase.commands.CreateOrderCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderNotifier orderNotifier;
    private final CacheService cacheService;
    private final CreateOrderProperties properties;

    /**
     * @throws DuplicatedOrderException if an order for this command has already been created
     */
    @Transactional
    public void execute(final CreateOrderCommand command) throws DuplicatedOrderException {
        Objects.requireNonNull(command, "command cannot be null.");
        final var order = CreateOrderCommand.toDomain(command);

        var deduplicationKey = buildDeduplicationKey(command);
        ensureNotDuplicated(deduplicationKey);

        try {
            var orderCreated = orderRepository.saveOrder(order);
            orderNotifier.notify(new OrderCreatedEvent(orderCreated));
        } catch (Exception e) {
            log.error("Error while saving order", e);
            cacheService.evict(deduplicationKey);
            throw e;
        }

    }

    private void ensureNotDuplicated(String deduplicationKey) throws DuplicatedOrderException {
        var inserted = cacheService.putIfAbsent(deduplicationKey, Strings.EMPTY, properties.getDeduplicationTtl());
        if (!inserted) {
            throw new DuplicatedOrderException("Order already created for key: " + deduplicationKey);
        }
    }

    private String buildDeduplicationKey(CreateOrderCommand command) {
        return String.format(properties.getDeduplicationKeyPattern(),
                             command.customerId(),
                             command.hashCode());
    }


}
