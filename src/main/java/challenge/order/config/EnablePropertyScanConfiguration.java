package challenge.order.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import challenge.order.dataprovider.messaging.producer.OrderEventTopics;

@EnableConfigurationProperties(value = {OrderEventTopics.class})
public class EnablePropertyScanConfiguration {

}
