package challenge.order.entrypoint.http;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import challenge.order.domain.dataprovider.repository.OrderRepository;
import challenge.order.domain.dataprovider.repository.PaginationRequest;
import challenge.order.entrypoint.http.mapper.OrderResponseMapper;
import challenge.order.entrypoint.http.payload.OrderResponse;
import challenge.order.entrypoint.http.payload.OrderSearchCriteriaRequestParam;
import challenge.order.entrypoint.http.payload.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import static challenge.order.entrypoint.http.mapper.PageResponseMapper.toPageResponse;

@RestController
@Validated
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Orders Management")
public class OrdersController {

    private final OrderRepository orderRepository;
    private final OrderResponseMapper orderResponseMapper;

    @Operation(summary = "Get orders by criteria")
    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> getOrderByCriteria(
        @ParameterObject final OrderSearchCriteriaRequestParam searchCriteriaRequestParam,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "20") Integer size
    ) {
        var result = orderRepository.findWithCriteria(searchCriteriaRequestParam, new PaginationRequest(page, size));

        var pageResponse = toPageResponse(result, orderResponseMapper::toResponse);
        return ResponseEntity.ok(pageResponse);

    }

    @Operation(summary = "Get orders by id")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable final UUID orderId) {
        return orderRepository
            .getOrderById(orderId)
            .map(order ->
                     new ResponseEntity<>(orderResponseMapper.toResponse(order), HttpStatus.OK)
            ).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));


    }


}
