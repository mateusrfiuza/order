package challenge.order.entrypoint.http;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import challenge.order.entrypoint.http.payload.OrderCreationRequest;
import challenge.order.events.Item;
import challenge.order.events.OrderCreationRequestSchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@Tag(name = "Internal Endpoint", description = "Only for admin purposes")
@RequiredArgsConstructor
public class InternalController {

    private final KafkaTemplate<String, OrderCreationRequestSchema> kafkaTemplate;

    @Operation(
        summary = "Trigger order creation flow",
        description = "Publishes an OrderCreationRequestSchema event to the 'order_creation_requested_event' Kafka topic to start the asynchronous order creation workflow."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Order creation payload",
        required = true,
        content = @Content(
            schema = @Schema(implementation = OrderCreationRequest.class)
        )
    )
    @PostMapping(
        path = "/orders",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> startCreationOrderFlow(
        @Valid @RequestBody OrderCreationRequest request
    ) {
        var schema = new OrderCreationRequestSchema();
        schema.setCustomerId(request.getCustomerId());
        schema.setSellerId(request.getSellerId());

        var avroItems = request.getItems().stream()
            .map(dto -> {
                var item = new Item();
                item.setProductId(dto.getProductId());
                item.setQuantity(dto.getQuantity());
                item.setPrice(dto.getPrice());
                return item;
            })
            .collect(Collectors.toList());
        schema.setItems(avroItems);

        kafkaTemplate.send(
            "order_creation_requested_event",
            schema.getCustomerId().toString(),
            schema
        );
        return ResponseEntity.accepted().build();
    }
}
