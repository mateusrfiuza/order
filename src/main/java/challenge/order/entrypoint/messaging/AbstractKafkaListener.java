package challenge.order.entrypoint.messaging;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.springframework.kafka.support.Acknowledgment;

import challenge.order.domain.exception.DuplicatedOrderException;

public abstract class AbstractKafkaListener<K, V> {

    protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

    protected abstract void process(V event) throws Exception;

    protected abstract String topic();

    public final void consume(ConsumerRecord<K, V> record, Acknowledgment ack) throws Exception {
        if (record == null || record.value() == null) {
            log.warn("Null or empty record received, skipping.");
            ack.acknowledge();
            return;
        }

        log.info("Consumed [topic={}, key={}, value={}]", topic(), record.key(), record.value());

        try {
            process(record.value());
            ack.acknowledge();
        } catch (DuplicatedOrderException e) {
            log.warn("Duplicate detected → ignoring. [topic={}, key={}, value={}]", topic(), record.key(), record.value());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error while processing message", e);
            throw e;
        }
    }


}
