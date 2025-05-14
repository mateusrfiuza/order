package challenge.order.dataprovider.messaging;

import org.springframework.stereotype.Component;

import challenge.order.dataprovider.messaging.producer.KafkaProducer;
import challenge.order.dataprovider.messaging.producer.OrderEventFactory;
import challenge.order.domain.dataprovider.event.OrderEvent;
import challenge.order.domain.dataprovider.event.OrderNotifier;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderNotifierImpl implements OrderNotifier {

    private final KafkaProducer kafkaProducer;
    private final OrderEventFactory eventFactory;

    @Override
    public void notify(OrderEvent event) {
        var message = eventFactory.create(event);
        kafkaProducer.send(message);
    }


}
