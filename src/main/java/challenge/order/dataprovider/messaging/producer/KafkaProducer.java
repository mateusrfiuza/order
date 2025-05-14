package challenge.order.dataprovider.messaging.producer;

import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer implements GenericProducer<Message<GenericRecord>> {

    private final KafkaTemplate<String, Message<GenericRecord>> kafkaTemplate;

    @Override
    @Async
    public void send(final Message<GenericRecord> value) {
        final var key = value.getHeaders().get(KafkaHeaders.KEY);
        final var topic = Objects.requireNonNull(value.getHeaders().get(KafkaHeaders.TOPIC)).toString();
        final var messageId = UUID.fromString(Objects.requireNonNull(value.getHeaders().get(MessageHeaders.ID)).toString());
        log.info("Sending message to topic [ {} ], key [ {} ], messageId[ {} ]", topic, key, messageId);
        kafkaTemplate.send(value);
    }

}
