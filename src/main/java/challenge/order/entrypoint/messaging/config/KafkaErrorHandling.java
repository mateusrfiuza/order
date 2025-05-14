package challenge.order.entrypoint.messaging.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class KafkaErrorHandling {

  @Value("${spring.kafka.bootstrap-servers}")
  private String kafkaBootstrapServers;

  @Bean
  public ProducerFactory<?, ?> dltProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<?, ?> dltKafkaTemplate() {
    return new KafkaTemplate<>(dltProducerFactory());
  }

  @Bean
  public DefaultErrorHandler errorHandler(KafkaTemplate<?, ?> dltKafkaTemplate) {
    var recoverer = new DeadLetterPublishingRecoverer(
            dltKafkaTemplate,
            (cr, ex) -> {
              log.error("Error consuming [topic={}, key={}, value={}]", cr.topic(), cr.key(), cr.value(), ex);
              return new TopicPartition(cr.topic() + "-dlt", cr.partition());
            }
        );

    var backOff = new FixedBackOff(0, 3L);
    var defaultErrorHandler =  new DefaultErrorHandler(recoverer, backOff);

    defaultErrorHandler.setLogLevel(KafkaException.Level.ERROR);
    defaultErrorHandler.setAckAfterHandle(true);

    defaultErrorHandler.addNotRetryableExceptions(
        DeserializationException.class,
        SerializationException.class,
        RecordDeserializationException.class,
        IllegalArgumentException.class
    );

    return defaultErrorHandler;
  }

}
