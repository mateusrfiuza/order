package challenge.order.domain.dataprovider.event;

public interface OrderNotifier {

    void notify(OrderEvent event);
}
