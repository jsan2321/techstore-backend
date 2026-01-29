package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.AddProductUseCase;
import com.ecoapi.goodshopping.product.application.port.out.BrandRepositoryPort;
import com.ecoapi.goodshopping.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.goodshopping.product.application.port.out.ProductEventPublisherPort;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.application.service.dto.ProductCommand;
import com.ecoapi.goodshopping.product.domain.events.ProductCreatedEvent;
import com.ecoapi.goodshopping.product.domain.exception.ProductAlreadyExistsException;
import com.ecoapi.goodshopping.product.domain.model.*;

/**
 * Application Service for adding new products
 * Single Responsibility: Handle product creation business logic
 */
public class AddProductService implements AddProductUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final BrandRepositoryPort brandRepository;
    private final ProductEventPublisherPort eventPublisher;
    
    public AddProductService(ProductRepositoryPort productRepository,
                            CategoryRepositoryPort categoryRepository,
                            BrandRepositoryPort brandRepository,
                            ProductEventPublisherPort eventPublisher) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Product addProduct(ProductCommand command) {
        // Validate that brand exists first
        Brand brand = brandRepository.findById(BrandId.of(command.brandId()))
                .orElseThrow(() -> new com.ecoapi.goodshopping.product.domain.exception.BrandNotFoundException(
                    "Brand not found with id: " + command.brandId()));
        
        // Check if product already exists
        if (productRepository.existsByNameAndBrandId(command.name(), command.brandId())) {
            throw new ProductAlreadyExistsException(
                brand.getName() + " " + command.name() + " already exists");
        }
        
        // Validate that category exists - no auto-creation
        Category category = categoryRepository.findById(CategoryId.of(command.categoryId()))
                .orElseThrow(() -> new com.ecoapi.goodshopping.product.domain.exception.CategoryNotFoundException(
                    "Category not found with id: " + command.categoryId()));
        
        // Create domain product using factory method
        Product product = Product.create(
                command.name(),
                brand,
                Money.of(command.price()),
                command.inventory(),
                command.description(),
                category
        );
        
        // Save product
        Product savedProduct = productRepository.save(product);
        
        // Publish domain event
        ProductCreatedEvent event = new ProductCreatedEvent(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getBrand().getName(),
                savedProduct.getPrice().value(),
                savedProduct.getInventory(),
                savedProduct.getCategory().getName()
        );
        eventPublisher.publish(event);
        
        return savedProduct;
    }
}
