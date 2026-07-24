package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import com.ecoapi.techstore.product.application.port.in.UpdateProductUseCase;
import com.ecoapi.techstore.product.application.port.out.BrandRepositoryPort;
import com.ecoapi.techstore.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.techstore.product.application.port.out.ProductEventPublisherPort;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.application.service.dto.ProductCommand;
import com.ecoapi.techstore.product.domain.events.ProductUpdatedEvent;
import com.ecoapi.techstore.product.domain.exception.BrandNotFoundException;
import com.ecoapi.techstore.product.domain.exception.CategoryNotFoundException;
import com.ecoapi.techstore.product.domain.exception.ProductNotFoundException;
import com.ecoapi.techstore.product.domain.model.*;

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
                .orElseThrow(() -> new BrandNotFoundException(
                    "Brand not found with id: " + command.brandId()));

        // Validate and update category if changed
        Category category = categoryRepository.findById(CategoryId.of(command.categoryId()))
                                              .orElseThrow(() -> new CategoryNotFoundException(
                                                      "Category not found with id: " + command.categoryId()));
        
        // Update product details
        product.updateDetails(
                command.name(),
                brand,
                category,
                Money.of(command.price()),
                command.description()
        );
        
        // Update stock
        int stockDiff = command.stock() - product.getStock();
        if (stockDiff > 0) {
            product.addStock(stockDiff);
        } else if (stockDiff < 0) {
            product.reduceStock(Math.abs(stockDiff));
        }

        if (command.applyDiscount()) {
            product.applyDiscount(command.discountPercentage());
        } else {
            product.removeDiscount();
        }
        product.setFeatured(command.featured());

        // Save product
        Product savedProduct = productRepository.save(product);
        
        // Publish domain event
        ProductUpdatedEvent event = new ProductUpdatedEvent(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getBrand().getName(),
                savedProduct.getCategory().getName()
        );
        eventPublisher.publish(event);
        
        return savedProduct;
    }
}
