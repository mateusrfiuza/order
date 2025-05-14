package challenge.order.domain.dataprovider.repository;

import java.util.UUID;

public interface OrderSearchCriteria {

    UUID getCustomerId();
    UUID getSellerId();
    OrderByDirection getDirection();

}
