package challenge.order.dataprovider.messaging.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;

@Configuration
@Profile("!test")
public class KafkaProducerConfig {

    private static final int MAX_PUBLISH_ATTEMPTS = Integer.MAX_VALUE;
    private static final int MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION = 1;
    private static final int RETRY_BACKOFF_MS_CONFIG = 3_000;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${spring.kafka.properties.schema.registry.url}")
    private String schemaRegistryURL;

    private final KafkaProperties properties;


    public KafkaProducerConfig(final KafkaProperties properties) {
        this.properties = properties;
    }


    @Bean
    public <T1, T2> ProducerFactory<T1, T2> producerFactory() {
        final Map<String, Object> configProps = properties.buildProducerProperties(new DefaultSslBundleRegistry());
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        configProps.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryURL);

        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION);
        configProps.put(ProducerConfig.RETRIES_CONFIG, MAX_PUBLISH_ATTEMPTS);

        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, RETRY_BACKOFF_MS_CONFIG);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<?, ?> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


}
