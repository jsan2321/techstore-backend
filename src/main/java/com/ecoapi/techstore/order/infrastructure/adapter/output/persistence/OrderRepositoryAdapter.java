package com.ecoapi.techstore.order.infrastructure.adapter.output.persistence;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.order.application.port.out.OrderRepositoryPort;
import com.ecoapi.techstore.order.application.service.dto.SearchOrderCriteria;
import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.domain.model.OrderId;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.entity.OrderEntity;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.mapper.OrderPersistenceMapper;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.repository.JpaOrderRepository;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.specification.OrderSpecification;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OrderRepositoryAdapter implements OrderRepositoryPort {
    
    private final JpaOrderRepository jpaOrderRepository;
    private final OrderPersistenceMapper mapper;
    
    public OrderRepositoryAdapter(JpaOrderRepository jpaOrderRepository,
                                 OrderPersistenceMapper mapper) {
        this.jpaOrderRepository = jpaOrderRepository;
        this.mapper = mapper;
    }
    
    @Override
    @Transactional
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity savedEntity = jpaOrderRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(OrderId orderId) {
        return jpaOrderRepository.findById(orderId.getValue())
                .map(mapper::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByUserId(UserId userId) {
        return jpaOrderRepository.findByUserId(userId.value()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return jpaOrderRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public PagedResult<Order> search(SearchOrderCriteria criteria) {
        Sort sort = Sort.by(
            criteria.sortDirection().equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC,
            criteria.sortBy()
        );
        
        Pageable pageable = PageRequest.of(criteria.page(), criteria.size(), sort);
        Page<OrderEntity> page = jpaOrderRepository.findAll(
            OrderSpecification.withCriteria(criteria), 
            pageable
        );
        
        List<Order> orders = page.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
        
        return PagedResult.of(orders, page.getNumber(), page.getSize(), page.getTotalElements());
    }
    
    @Override
    public boolean existsById(OrderId orderId) {
        return jpaOrderRepository.existsById(orderId.getValue());
    }
}
