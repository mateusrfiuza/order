package challenge.order.entrypoint.http.payload;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreationRequest {

    @NotNull
    private UUID customerId;

    @NotNull
    private UUID sellerId;

    @NotNull
    private Instant sourceOriginDate;

    @NotEmpty
    private Set<ItemRequest> items;

    @Data
    public static class ItemRequest {
        @NotNull
        private UUID productId;

        @Min(1)
        private long quantity;

        @NotNull
        @DecimalMin("0.00")
        private BigDecimal price;
    }
}
