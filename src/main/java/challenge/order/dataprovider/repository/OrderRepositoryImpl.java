package challenge.order.dataprovider.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import challenge.order.dataprovider.database.OrderDAO;
import challenge.order.dataprovider.database.OrderSearchSpecifications;
import challenge.order.dataprovider.database.PageImpl;
import challenge.order.dataprovider.database.mapper.OrderEntityMapper;
import challenge.order.domain.dataprovider.repository.OrderRepository;
import challenge.order.domain.dataprovider.repository.OrderSearchCriteria;
import challenge.order.domain.dataprovider.repository.Page;
import challenge.order.domain.dataprovider.repository.PaginationRequest;
import challenge.order.domain.entity.Order;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderDAO orderDAO;
    private final OrderEntityMapper orderEntityMapper;

    @Override
    public Order saveOrder(final Order order) {
        final var savedOrderEntity = orderDAO.save(orderEntityMapper.toEntity(order));
        return orderEntityMapper.toDomain(savedOrderEntity);
    }

    @Override
    public Optional<Order> getOrderById(UUID orderId) {
        return orderDAO.findById(orderId).map(orderEntityMapper::toDomain);
    }

    @Override
    public Page<Order> findWithCriteria(OrderSearchCriteria searchCriteria, PaginationRequest pageRequest) {
        var spec = Specification
            .where(OrderSearchSpecifications.hasCustomerId(searchCriteria.getCustomerId()))
            .and(OrderSearchSpecifications.hasSellerId(searchCriteria.getSellerId()))
            .and(OrderSearchSpecifications.orderByCreatedAt(searchCriteria.getDirection()));

        var pageResult = orderDAO.findAll(spec, PageRequest.of(pageRequest.page(), pageRequest.size()));

        var content = pageResult
            .stream()
            .map(orderEntityMapper::toDomain)
            .toList();

        return new PageImpl<>(
            content,
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements()
        );

    }
}
