package challenge.order.entrypoint.messaging.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
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

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class KafkaErrorHandling {

  @Value("${spring.kafka.bootstrap-servers}")
  private String kafkaBootstrapServers;

  private final KafkaProperties properties;

  @Value("${spring.kafka.properties.schema.registry.url}")
  private String schemaRegistryURL;


  public KafkaErrorHandling(final KafkaProperties properties) {
    this.properties = properties;
  }

  @Bean
  public ProducerFactory<?, ?> dltProducerFactory() {
    var configProps = properties.buildProducerProperties(new DefaultSslBundleRegistry());
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
    configProps.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryURL);

    return new DefaultKafkaProducerFactory<>(configProps);

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
              log.error("Error consuming [topic={}, key={}, value={}]", cr.topic(), cr.key(), cr.value(), ex.getCause());

              if (skipDltPublication(ex)) {
                log.error("Skipping DLT for serialization-related error[topic={}, key={}, value={}]", cr.topic(), cr.key(), cr.value(), ex.getCause());
                return null;
              }

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

  private boolean skipDltPublication(Throwable ex) {
    Throwable cause = ex;
    while (cause != null && cause != cause.getCause()) {
      if (cause instanceof DeserializationException || cause instanceof SerializationException) {
        return true;
      }
      cause = cause.getCause();
    }
    return false;
  }

}
