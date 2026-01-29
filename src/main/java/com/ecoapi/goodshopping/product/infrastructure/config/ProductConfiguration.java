package com.ecoapi.goodshopping.product.infrastructure.config;

import com.ecoapi.goodshopping.product.application.port.in.*;
import com.ecoapi.goodshopping.product.application.port.out.BrandRepositoryPort;
import com.ecoapi.goodshopping.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.goodshopping.product.application.port.out.ProductEventPublisherPort;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.application.port.out.S3StoragePort;
import com.ecoapi.goodshopping.product.application.service.*;
import com.ecoapi.goodshopping.product.infrastructure.adapter.TransactionalUseCaseWrapper;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.events.ProductEventPublisherAdapter;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.BrandRepositoryAdapter;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.mapper.ProductPersistenceMapper;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.CategoryRepositoryAdapter;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.ProductRepositoryAdapter;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.repository.JpaBrandRepository;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.repository.JpaCategoryRepository;
import com.ecoapi.goodshopping.product.infrastructure.adapter.output.persistence.repository.JpaProductRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for Product Bounded Context
 * Wires all infrastructure adapters and application services
 * This is the ONLY place where framework-specific wiring happens
 * Application layer remains framework-agnostic
 * 
 * Pattern: Decorator pattern with transactional wrappers
 * - Pure services are instantiated (framework-agnostic)
 * - Wrapped with transactional behavior (infrastructure concern)
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
                                                       JpaBrandRepository jpaBrandRepository,
                                                       ProductPersistenceMapper mapper) {
        return new ProductRepositoryAdapter(jpaProductRepository, jpaBrandRepository, mapper);
    }
    
    @Bean
    public CategoryRepositoryPort categoryRepositoryPort(JpaCategoryRepository jpaCategoryRepository,
                                                        ProductPersistenceMapper mapper) {
        return new CategoryRepositoryAdapter(jpaCategoryRepository, mapper);
    }
    
    @Bean
    public BrandRepositoryPort brandRepositoryPort(JpaBrandRepository jpaBrandRepository,
                                                   ProductPersistenceMapper mapper) {
        return new BrandRepositoryAdapter(jpaBrandRepository, mapper);
    }
    
    @Bean
    public ProductEventPublisherPort productEventPublisherPort(ApplicationEventPublisher eventPublisher) {
        return new ProductEventPublisherAdapter(eventPublisher);
    }
    
    // ==================== Application Services (Use Cases) ====================
    // Note: Services are wrapped with transactional behavior at infrastructure boundary
    
    @Bean
    public AddProductUseCase addProductUseCase(ProductRepositoryPort productRepository,
                                               CategoryRepositoryPort categoryRepository,
                                               BrandRepositoryPort brandRepository,
                                               ProductEventPublisherPort eventPublisher) {
        AddProductUseCase service = new AddProductService(productRepository, categoryRepository, brandRepository, eventPublisher);
        return new TransactionalUseCaseWrapper.TransactionalAddProductUseCase(service);
    }
    
    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductRepositoryPort productRepository,
                                                     CategoryRepositoryPort categoryRepository,
                                                     BrandRepositoryPort brandRepository,
                                                     ProductEventPublisherPort eventPublisher) {
        UpdateProductUseCase service = new UpdateProductService(productRepository, categoryRepository, brandRepository, eventPublisher);
        return new TransactionalUseCaseWrapper.TransactionalUpdateProductUseCase(service);
    }
    
    @Bean
    public DeleteProductUseCase deleteProductUseCase(ProductRepositoryPort productRepository) {
        DeleteProductUseCase service = new DeleteProductService(productRepository);
        return new TransactionalUseCaseWrapper.TransactionalDeleteProductUseCase(service);
    }
    
    @Bean
    public GetProductByIdUseCase getProductByIdUseCase(ProductRepositoryPort productRepository) {
        GetProductByIdUseCase service = new GetProductByIdService(productRepository);
        return new TransactionalUseCaseWrapper.TransactionalGetProductByIdUseCase(service);
    }
    
    @Bean
    public GetAllProductsUseCase getAllProductsUseCase(ProductRepositoryPort productRepository) {
        GetAllProductsUseCase service = new GetAllProductsService(productRepository);
        return new TransactionalUseCaseWrapper.TransactionalGetAllProductsUseCase(service);
    }
    
    @Bean
    public GetProductsByCategoryUseCase getProductsByCategoryUseCase(ProductRepositoryPort productRepository) {
        GetProductsByCategoryUseCase service = new GetProductsByCategoryService(productRepository);
        return new TransactionalUseCaseWrapper.TransactionalGetProductsByCategoryUseCase(service);
    }
    
    @Bean
    public GetProductsByBrandUseCase getProductsByBrandUseCase(ProductRepositoryPort productRepository) {
        GetProductsByBrandUseCase service = new GetProductsByBrandService(productRepository);
        return new TransactionalUseCaseWrapper.TransactionalGetProductsByBrandUseCase(service);
    }
    
    @Bean
    public SearchProductsUseCase searchProductsUseCase(ProductRepositoryPort productRepository) {
        SearchProductsUseCase service = new SearchProductsService(productRepository);
        return new TransactionalUseCaseWrapper.TransactionalSearchProductsUseCase(service);
    }
    
    @Bean
    public SearchProductsByCriteriaUseCase searchProductsByCriteriaUseCase(ProductRepositoryPort productRepository) {
        SearchProductsByCriteriaUseCase service = new SearchProductsByCriteriaService(productRepository);
        return new TransactionalUseCaseWrapper.TransactionalSearchProductsByCriteriaUseCase(service);
    }
    
    @Bean
    public UploadProductImageUseCase uploadProductImageUseCase(ProductRepositoryPort productRepository,
                                                                S3StoragePort s3Storage) {
        UploadProductImageUseCase service = new UploadProductImageService(productRepository, s3Storage);
        return new TransactionalUseCaseWrapper.TransactionalUploadProductImageUseCase(service);
    }
    
    @Bean
    public DeleteProductImageUseCase deleteProductImageUseCase(ProductRepositoryPort productRepository,
                                                                S3StoragePort s3Storage) {
        DeleteProductImageUseCase service = new DeleteProductImageService(productRepository, s3Storage);
        return new TransactionalUseCaseWrapper.TransactionalDeleteProductImageUseCase(service);
    }
    
    // ==================== Category Use Cases ====================
    
    @Bean
    public AddCategoryUseCase addCategoryUseCase(CategoryRepositoryPort categoryRepository) {
        AddCategoryUseCase service = new AddCategoryService(categoryRepository);
        return new TransactionalUseCaseWrapper.TransactionalAddCategoryUseCase(service);
    }
    
    @Bean
    public GetAllCategoriesUseCase getAllCategoriesUseCase(JpaCategoryRepository jpaCategoryRepository,
                                                            ProductPersistenceMapper mapper) {
        GetAllCategoriesUseCase service = new GetAllCategoriesService(jpaCategoryRepository, mapper);
        return new TransactionalUseCaseWrapper.TransactionalGetAllCategoriesUseCase(service);
    }
    
    @Bean
    public GetCategoryByIdUseCase getCategoryByIdUseCase(CategoryRepositoryPort categoryRepository) {
        GetCategoryByIdUseCase service = new GetCategoryByIdService(categoryRepository);
        return new TransactionalUseCaseWrapper.TransactionalGetCategoryByIdUseCase(service);
    }
    
    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase(CategoryRepositoryPort categoryRepository) {
        UpdateCategoryUseCase service = new UpdateCategoryService(categoryRepository);
        return new TransactionalUseCaseWrapper.TransactionalUpdateCategoryUseCase(service);
    }
    
    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase(JpaCategoryRepository jpaCategoryRepository) {
        DeleteCategoryUseCase service = new DeleteCategoryService(jpaCategoryRepository);
        return new TransactionalUseCaseWrapper.TransactionalDeleteCategoryUseCase(service);
    }
    
    // ==================== Brand Use Cases ====================
    
    @Bean
    public AddBrandUseCase addBrandUseCase(BrandRepositoryPort brandRepository) {
        AddBrandUseCase service = new AddBrandService(brandRepository);
        return new TransactionalUseCaseWrapper.TransactionalAddBrandUseCase(service);
    }
    
    @Bean
    public GetAllBrandsUseCase getAllBrandsUseCase(JpaBrandRepository jpaBrandRepository,
                                                   ProductPersistenceMapper mapper) {
        GetAllBrandsUseCase service = new GetAllBrandsService(jpaBrandRepository, mapper);
        return new TransactionalUseCaseWrapper.TransactionalGetAllBrandsUseCase(service);
    }
    
    @Bean
    public GetBrandByIdUseCase getBrandByIdUseCase(BrandRepositoryPort brandRepository) {
        GetBrandByIdUseCase service = new GetBrandByIdService(brandRepository);
        return new TransactionalUseCaseWrapper.TransactionalGetBrandByIdUseCase(service);
    }
    
    @Bean
    public UpdateBrandUseCase updateBrandUseCase(BrandRepositoryPort brandRepository) {
        UpdateBrandUseCase service = new UpdateBrandService(brandRepository);
        return new TransactionalUseCaseWrapper.TransactionalUpdateBrandUseCase(service);
    }
    
    @Bean
    public DeleteBrandUseCase deleteBrandUseCase(JpaBrandRepository jpaBrandRepository) {
        DeleteBrandUseCase service = new DeleteBrandService(jpaBrandRepository);
        return new TransactionalUseCaseWrapper.TransactionalDeleteBrandUseCase(service);
    }
}
