package com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.repository;

import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
    List<OrderEntity> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
