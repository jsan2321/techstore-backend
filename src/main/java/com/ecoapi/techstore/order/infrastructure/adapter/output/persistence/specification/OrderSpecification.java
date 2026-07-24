package com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.specification;

import com.ecoapi.techstore.order.application.service.dto.SearchOrderCriteria;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.entity.OrderEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for dynamic Order queries
 */
public class OrderSpecification {
    
    public static Specification<OrderEntity> withCriteria(SearchOrderCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (criteria.userId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), criteria.userId()));
            }
            
            if (criteria.status() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.status()));
            }
            
            if (criteria.startDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("orderDate"), criteria.startDate()));
            }
            
            if (criteria.endDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("orderDate"), criteria.endDate()));
            }
            
            if (criteria.minAmount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("totalAmount"), criteria.minAmount()));
            }
            
            if (criteria.maxAmount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("totalAmount"), criteria.maxAmount()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}