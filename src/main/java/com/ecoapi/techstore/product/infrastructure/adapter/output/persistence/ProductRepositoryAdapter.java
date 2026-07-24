package com.ecoapi.techstore.product.infrastructure.adapter.output.persistence;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.application.service.dto.AdminProductListReadModel;
import com.ecoapi.techstore.product.application.service.dto.AdminProductSearchCriteria;
import com.ecoapi.techstore.product.application.service.dto.ProductReadModel;
import com.ecoapi.techstore.product.application.service.dto.ProductSearchCriteria;
import com.ecoapi.techstore.product.domain.model.Product;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.entity.ProductEntity;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.mapper.ProductPersistenceMapper;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.repository.JpaProductRepository;
import com.ecoapi.techstore.product.infrastructure.adapter.output.persistence.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapter implementing ProductRepositoryPort
 * Bridges domain and JPA persistence
 */
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    
    private final JpaProductRepository jpaRepository;
    private final ProductPersistenceMapper mapper;
    
    public ProductRepositoryAdapter(JpaProductRepository jpaRepository,
                                   ProductPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);

        ProductEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public void deleteById(ProductId id) {
        jpaRepository.deleteById(Objects.requireNonNull(id.value()));
    }
    
    @Override
    public Optional<Product> findById(ProductId id) {
        return jpaRepository.findById(Objects.requireNonNull(id.value()))
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
    
    @Override
        public PagedResult<ProductReadModel> searchByCriteria(ProductSearchCriteria criteria) {
        Sort sort = buildSort(criteria.sortBy(), criteria.sortDirection(), false);
        Pageable pageable = PageRequest.of(criteria.page(), criteria.size(), Objects.requireNonNull(sort));

        Page<ProductEntity> page = jpaRepository.findAll(ProductSpecification.publicCriteria(criteria), pageable);
        List<ProductReadModel> content = page.getContent().stream()
            .map(this::toReadModel)
            .collect(Collectors.toList());

        return PagedResult.of(content, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    private ProductReadModel toReadModel(ProductEntity entity) {
        return ProductReadModel.create(
                entity.getId(),
                entity.getName(),
                entity.getBrand() != null ? entity.getBrand().getName() : null,
                entity.getPrice(),
                entity.getDiscountPercentage(),
            entity.getStock(),
                entity.getDescription(),
                entity.getCategory() != null ? entity.getCategory().getName() : null,
                entity.getImageUrl()
        );
    }

    @Override
        public PagedResult<AdminProductListReadModel> searchByAdminCriteria(AdminProductSearchCriteria criteria) {
        Sort sort = buildSort(criteria.sortBy(), criteria.sortDirection(), true);
        Pageable pageable = PageRequest.of(criteria.page(), criteria.size(), Objects.requireNonNull(sort));

        Page<ProductEntity> page = jpaRepository.findAll(ProductSpecification.adminCriteria(criteria), pageable);
        List<AdminProductListReadModel> content = page.getContent().stream()
            .map(this::toAdminListReadModel)
            .collect(Collectors.toList());

        return PagedResult.of(content, page.getNumber(), page.getSize(), page.getTotalElements());
    }

        private AdminProductListReadModel toAdminListReadModel(ProductEntity entity) {
        return AdminProductListReadModel.create(
                entity.getId(),
                entity.getName(),
            entity.getCategory() != null ? entity.getCategory().getName() : null,
                entity.getPrice(),
                entity.getDiscountPercentage(),
            entity.getStock()
        );
    }

        private Sort buildSort(String sortBy, String sortDirection, boolean adminSearch) {
        Set<String> allowed = adminSearch
            ? Set.of("id", "name", "price", "stock", "active", "featured")
            : Set.of("id", "name", "price", "stock");

        String safeSortBy = allowed.contains(sortBy) ? sortBy : "name";
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection)
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;

        return Sort.by(direction, safeSortBy);
        }

}
