package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.UpdateProductUseCase;
import com.ecoapi.goodshopping.product.application.port.out.BrandRepositoryPort;
import com.ecoapi.goodshopping.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.goodshopping.product.application.port.out.ProductEventPublisherPort;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.application.service.dto.ProductCommand;
import com.ecoapi.goodshopping.product.domain.events.ProductUpdatedEvent;
import com.ecoapi.goodshopping.product.domain.exception.ProductNotFoundException;
import com.ecoapi.goodshopping.product.domain.model.*;

/**
 * Application Service for updating products
 * Single Responsibility: Handle product update business logic
 */
public class UpdateProductService implements UpdateProductUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final BrandRepositoryPort brandRepository;
    private final ProductEventPublisherPort eventPublisher;
    
    public UpdateProductService(ProductRepositoryPort productRepository,
                               CategoryRepositoryPort categoryRepository,
                               BrandRepositoryPort brandRepository,
                               ProductEventPublisherPort eventPublisher) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Product updateProduct(Long productId, ProductCommand command) {
        Product product = productRepository.findById(ProductId.of(productId))
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        // Validate and get brand
        Brand brand = brandRepository.findById(BrandId.of(command.brandId()))
                .orElseThrow(() -> new com.ecoapi.goodshopping.product.domain.exception.BrandNotFoundException(
                    "Brand not found with id: " + command.brandId()));
        
        // Update product details
        product.updateDetails(
                command.name(),
                brand,
                Money.of(command.price()),
                command.description()
        );
        
        // Update inventory
        int inventoryDiff = command.inventory() - product.getInventory();
        if (inventoryDiff > 0) {
            product.addStock(inventoryDiff);
        } else if (inventoryDiff < 0) {
            product.reduceStock(Math.abs(inventoryDiff));
        }
        
        // Validate and update category if changed
        Category category = categoryRepository.findById(CategoryId.of(command.categoryId()))
                .orElseThrow(() -> new com.ecoapi.goodshopping.product.domain.exception.CategoryNotFoundException(
                    "Category not found with id: " + command.categoryId()));
        
        if (!product.getCategory().getId().equals(category.getId())) {
            product.changeCategory(category);
        }
        
        // Save product
        Product savedProduct = productRepository.save(product);
        
        // Publish domain event
        ProductUpdatedEvent event = new ProductUpdatedEvent(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getBrand().getName()
        );
        eventPublisher.publish(event);
        
        return savedProduct;
    }
}
