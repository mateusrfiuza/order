package challenge.order.entrypoint.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import challenge.order.domain.dataprovider.repository.OrderRepository;
import challenge.order.domain.entity.Order;
import challenge.order.domain.entity.OrderItem;
import challenge.order.domain.entity.OrderStatus;
import challenge.order.entrypoint.http.mapper.OrderResponseMapper;
import challenge.order.entrypoint.http.mapper.OrderResponseMapperImpl;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO CREATE OTHER TEST CASES FOR OTHER ENDPOINTS
@WebMvcTest(OrdersController.class)
@Import(OrderResponseMapperImpl.class)
class OrdersControllerTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private OrderResponseMapper orderResponseMapper;

  @MockitoBean
  private OrderRepository orderRepository;

  @Test
  void getOrderById_whenFound_returns200() throws Exception {
    var id = UUID.randomUUID();
    var order = new Order(
        id,
        UUID.randomUUID(),
        UUID.randomUUID(),
        Set.of(new OrderItem(UUID.randomUUID(), UUID.randomUUID(), 1L, BigDecimal.valueOf(10))),
        OrderStatus.CREATED,
        Instant.now()
    );

    var expectedJson = objectMapper.writeValueAsString(orderResponseMapper.toResponse(order));

    when(orderRepository.getOrderById(id)).thenReturn(Optional.of(order));

    mvc.perform(get("/orders/{orderId}", id))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedJson));
  }

  @Test
  void getOrderById_whenNotFound_returns404() throws Exception {
    var id = UUID.randomUUID();
    when(orderRepository.getOrderById(id)).thenReturn(Optional.empty());

    mvc.perform(get("/orders/{orderId}", id))
        .andExpect(status().isNotFound());
  }
}
