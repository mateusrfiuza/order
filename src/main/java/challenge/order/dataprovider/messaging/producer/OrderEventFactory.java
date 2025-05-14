package challenge.order.dataprovider.messaging.producer;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import challenge.order.dataprovider.database.mapper.OrderCreatedSchemaMapper;
import challenge.order.domain.dataprovider.event.OrderCreatedEvent;
import challenge.order.domain.dataprovider.event.OrderEvent;
import challenge.order.domain.entity.OrderStatus;
import lombok.RequiredArgsConstructor;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class OrderEventFactory {

    private final OrderEventTopics topics;
    private final OrderCreatedSchemaMapper orderCreatedSchemaMapper;

    private static final String TOPIC_HEADER = KafkaHeaders.TOPIC;
    private static final String KEY_HEADER = KafkaHeaders.KEY;
    private static final String KEY_NAME = "orderId";

    public Message<GenericRecord> create(final OrderEvent event) {
        requireNonNull(event, "event must not be null");
        var avro = createAvroRecord(event);
        return MessageBuilder
            .withPayload(avro)
            .setHeader(TOPIC_HEADER, topics.getDestinationTopic(event))
            .setHeader(KEY_HEADER,  avro.get(KEY_NAME).toString())
            .build();
    }

    private GenericRecord createAvroRecord(final OrderEvent event) {
        return switch (event.getOrder().getStatus()) {
            case OrderStatus.CREATED -> orderCreatedSchemaMapper.toAvro((OrderCreatedEvent) event);
            default -> throw new IllegalArgumentException("Unsupported event type");
        };
    }

}
