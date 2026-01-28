package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.UpdateProductUseCase;
import com.ecoapi.goodshopping.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.goodshopping.product.application.port.out.ProductEventPublisherPort;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.application.service.dto.UpdateProductCommand;
import com.ecoapi.goodshopping.product.domain.events.ProductUpdatedEvent;
import com.ecoapi.goodshopping.product.domain.exception.ProductNotFoundException;
import com.ecoapi.goodshopping.product.domain.model.Brand;
import com.ecoapi.goodshopping.product.domain.model.Category;
import com.ecoapi.goodshopping.product.domain.model.Money;
import com.ecoapi.goodshopping.product.domain.model.Product;
import com.ecoapi.goodshopping.product.domain.model.ProductId;

/**
 * Application Service for updating products
 * Single Responsibility: Handle product update business logic
 */
public class UpdateProductService implements UpdateProductUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final ProductEventPublisherPort eventPublisher;
    
    public UpdateProductService(ProductRepositoryPort productRepository,
                               CategoryRepositoryPort categoryRepository,
                               ProductEventPublisherPort eventPublisher) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Product updateProduct(Long productId, UpdateProductCommand command) {
        Product product = productRepository.findById(ProductId.of(productId))
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
        
        // Update product details
        product.updateDetails(
                command.name(),
                Brand.of(command.brand()),
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
        
        // Update category if changed
        Category category = categoryRepository.findByName(command.categoryName())
                .orElseGet(() -> categoryRepository.save(new Category(command.categoryName())));
        
        if (!product.getCategory().getName().equals(category.getName())) {
            product.changeCategory(category);
        }
        
        // Save product
        Product savedProduct = productRepository.save(product);
        
        // Publish domain event
        ProductUpdatedEvent event = new ProductUpdatedEvent(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getBrand().value()
        );
        eventPublisher.publish(event);
        
        return savedProduct;
    }
}
