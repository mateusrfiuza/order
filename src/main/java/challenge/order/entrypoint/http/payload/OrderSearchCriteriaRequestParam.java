package challenge.order.entrypoint.http.payload;

import java.util.UUID;

import challenge.order.domain.dataprovider.repository.OrderByDirection;
import challenge.order.domain.dataprovider.repository.OrderSearchCriteria;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderSearchCriteriaRequestParam implements OrderSearchCriteria {

  private UUID customerId;
  private UUID sellerId;
  @Schema(
      description = "Order by creation date",
      defaultValue = "DESC",
      allowableValues = { "ASC", "DESC" }
  )
  private OrderByDirection direction = OrderByDirection.DESC;

}
