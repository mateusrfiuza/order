package challenge.order.dataprovider.messaging.producer;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

import challenge.order.domain.dataprovider.event.OrderEvent;
import jakarta.validation.constraints.NotEmpty;
import lombok.Setter;


@Validated
@ConfigurationProperties("order.event")
@Setter
@Configuration
public class OrderEventTopics {

    @NotEmpty
    private Map<String, @NotEmpty String> destinationTopic;

    public String getDestinationTopic(final OrderEvent value) {
        return destinationTopic.get(value.getClass().getSimpleName());
    }

}

