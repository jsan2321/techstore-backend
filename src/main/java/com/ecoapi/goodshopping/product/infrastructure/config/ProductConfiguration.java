package com.ecoapi.goodshopping.product.infrastructure.config;

import com.ecoapi.goodshopping.product.application.port.in.*;
import com.ecoapi.goodshopping.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.goodshopping.product.application.port.out.ProductEventPublisherPort;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.application.service.*;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.events.ProductEventPublisherAdapter;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.mapper.ProductPersistenceMapper;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.CategoryRepositoryAdapter;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.ProductRepositoryAdapter;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.repository.JpaCategoryRepository;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.repository.JpaProductRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for Product Bounded Context
 * Wires all infrastructure adapters and application services
 */
@Configuration
public class ProductConfiguration {
    
    // ==================== Mappers ====================
    
    @Bean
    public ProductPersistenceMapper productPersistenceMapper() {
        return new ProductPersistenceMapper();
    }
    
    // ==================== Output Ports (Adapters) ====================
    
    @Bean
    public ProductRepositoryPort productRepositoryPort(JpaProductRepository jpaProductRepository,
                                                       ProductPersistenceMapper mapper) {
        return new ProductRepositoryAdapter(jpaProductRepository, mapper);
    }
    
    @Bean
    public CategoryRepositoryPort categoryRepositoryPort(JpaCategoryRepository jpaCategoryRepository,
                                                        ProductPersistenceMapper mapper) {
        return new CategoryRepositoryAdapter(jpaCategoryRepository, mapper);
    }
    
    @Bean
    public ProductEventPublisherPort productEventPublisherPort(ApplicationEventPublisher eventPublisher) {
        return new ProductEventPublisherAdapter(eventPublisher);
    }
    
    // ==================== Application Services (Use Cases) ====================
    
    @Bean
    public AddProductUseCase addProductUseCase(ProductRepositoryPort productRepository,
                                               CategoryRepositoryPort categoryRepository,
                                               ProductEventPublisherPort eventPublisher) {
        return new AddProductService(productRepository, categoryRepository, eventPublisher);
    }
    
    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductRepositoryPort productRepository,
                                                     CategoryRepositoryPort categoryRepository,
                                                     ProductEventPublisherPort eventPublisher) {
        return new UpdateProductService(productRepository, categoryRepository, eventPublisher);
    }
    
    @Bean
    public DeleteProductUseCase deleteProductUseCase(ProductRepositoryPort productRepository) {
        return new DeleteProductService(productRepository);
    }
    
    @Bean
    public GetProductByIdUseCase getProductByIdUseCase(ProductRepositoryPort productRepository) {
        return new GetProductByIdService(productRepository);
    }
    
    @Bean
    public GetAllProductsUseCase getAllProductsUseCase(ProductRepositoryPort productRepository) {
        return new GetAllProductsService(productRepository);
    }
    
    @Bean
    public GetProductsByCategoryUseCase getProductsByCategoryUseCase(ProductRepositoryPort productRepository) {
        return new GetProductsByCategoryService(productRepository);
    }
    
    @Bean
    public GetProductsByBrandUseCase getProductsByBrandUseCase(ProductRepositoryPort productRepository) {
        return new GetProductsByBrandService(productRepository);
    }
    
    @Bean
    public SearchProductsUseCase searchProductsUseCase(ProductRepositoryPort productRepository) {
        return new SearchProductsService(productRepository);
    }
}
