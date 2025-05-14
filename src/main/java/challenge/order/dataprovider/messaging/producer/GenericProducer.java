package challenge.order.dataprovider.messaging.producer;

public interface GenericProducer<V> {

    void send(V value);

}
