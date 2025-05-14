package challenge.order.entrypoint.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import challenge.order.domain.exception.DuplicatedOrderException;
import challenge.order.domain.usecase.CreateOrderUseCase;
import challenge.order.entrypoint.messaging.mapper.OrderCreationRequestMapper;
import challenge.order.events.OrderCreationRequestSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static challenge.order.entrypoint.messaging.config.KafkaListenerConfig.ORDER_CONTAINER_FACTORY;


@Component
@Slf4j
@RequiredArgsConstructor
public class OrderCreationRequestedListener extends AbstractKafkaListener<String, OrderCreationRequestSchema> {

    private final CreateOrderUseCase createOrderUseCase;
    private final OrderCreationRequestMapper mapper;

    private static final String TOPIC = "order_creation_requested_event";

    @KafkaListener(
            topics = TOPIC,
            containerFactory = ORDER_CONTAINER_FACTORY
    )
    public void listener(final ConsumerRecord<String, OrderCreationRequestSchema> consumerRecord, final Acknowledgment acknowledgment) throws Exception {
        consume(consumerRecord, acknowledgment);
    }

    @Override
    public void process(OrderCreationRequestSchema event) throws DuplicatedOrderException {
        var command = mapper.toCommand(event);
        createOrderUseCase.execute(command);
    }

    @Override
    public String topic() {
        return TOPIC;
    }


}
