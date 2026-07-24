package com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.specification;

import com.ecoapi.techstore.product.application.service.dto.AdminProductSearchCriteria;
import com.ecoapi.techstore.product.application.service.dto.ProductSearchCriteria;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.entity.ProductEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA specification for dynamic product searches.
 */
public class ProductSpecification {

    public static Specification<ProductEntity> publicCriteria(ProductSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isTrue(root.get("active")));

            if (criteria.category() != null && !criteria.category().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("category").get("name")),
                        criteria.category().toLowerCase()));
            }

            if (criteria.brand() != null && !criteria.brand().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("brand").get("name")),
                        criteria.brand().toLowerCase()));
            }

            if (criteria.minPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        getEffectivePriceExpression(root, criteriaBuilder),
                        criteria.minPrice()
                ));
            }

            if (criteria.maxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        getEffectivePriceExpression(root, criteriaBuilder),
                        criteria.maxPrice()
                ));
            }

            if (criteria.inStock() != null) {
                if (criteria.inStock()) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("stock"), 0));
                } else {
                    predicates.add(criteriaBuilder.equal(root.get("stock"), 0));
                }
            }

            if (criteria.featured() != null) {
                predicates.add(criteriaBuilder.equal(root.get("featured"), criteria.featured()));
            }

            if (criteria.nameContains() != null && !criteria.nameContains().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + criteria.nameContains().toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ProductEntity> adminCriteria(AdminProductSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.category() != null && !criteria.category().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("category").get("name")),
                        criteria.category().toLowerCase()));
            }

            if (criteria.brand() != null && !criteria.brand().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("brand").get("name")),
                        criteria.brand().toLowerCase()));
            }

            if (criteria.minPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        getEffectivePriceExpression(root, criteriaBuilder),
                        criteria.minPrice()
                ));
            }

            if (criteria.maxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        getEffectivePriceExpression(root, criteriaBuilder),
                        criteria.maxPrice()
                ));
            }

            if (criteria.inStock() != null) {
                if (criteria.inStock()) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("stock"), 0));
                } else {
                    predicates.add(criteriaBuilder.equal(root.get("stock"), 0));
                }
            }

            if (criteria.active() != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), criteria.active()));
            }

            if (criteria.featured() != null) {
                predicates.add(criteriaBuilder.equal(root.get("featured"), criteria.featured()));
            }

            if (criteria.nameContains() != null && !criteria.nameContains().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + criteria.nameContains().toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Expression<BigDecimal> getEffectivePriceExpression(
            Root<ProductEntity> root,
            CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.selectCase()
                .when(
                        criteriaBuilder.and(
                                criteriaBuilder.isNotNull(root.get("discountPercentage")),
                                criteriaBuilder.greaterThan(root.get("discountPercentage"), 0)
                        ),
                        criteriaBuilder.diff(
                                root.get("price"),
                                criteriaBuilder.quot(
                                        criteriaBuilder.prod(root.get("price"), root.get("discountPercentage")),
                                        100
                                )
                        )
                )
                .otherwise(root.get("price"))
                .as(BigDecimal.class);
    }
}
