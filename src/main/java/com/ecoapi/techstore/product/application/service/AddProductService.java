package com.ecoapi.techstore.product.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.Money;
import com.ecoapi.techstore.product.application.port.in.AddProductUseCase;
import com.ecoapi.techstore.product.application.port.out.BrandRepositoryPort;
import com.ecoapi.techstore.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.techstore.product.application.port.out.ProductEventPublisherPort;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.application.service.dto.ProductCommand;
import com.ecoapi.techstore.product.domain.events.ProductCreatedEvent;
import com.ecoapi.techstore.product.domain.exception.BrandNotFoundException;
import com.ecoapi.techstore.product.domain.exception.CategoryNotFoundException;
import com.ecoapi.techstore.product.domain.exception.ProductAlreadyExistsException;
import com.ecoapi.techstore.product.domain.model.*;

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
        // Check if product already exists
        if (productRepository.existsByName(command.name())) {
            throw new ProductAlreadyExistsException(
                command.name() + " already exists");
        }

        // Validate that brand exists - no auto-creation
        Brand brand = brandRepository.findById(BrandId.of(command.brandId()))
                                              .orElseThrow(() -> new BrandNotFoundException(
                                                      "Brand not found with id: " + command.brandId()));

        // Validate that category exists - no auto-creation
        Category category = categoryRepository.findById(CategoryId.of(command.categoryId()))
                .orElseThrow(() -> new CategoryNotFoundException(
                    "Category not found with id: " + command.categoryId()));
        
        // Create domain product using factory method
        Product product = Product.create(
                command.name(),
                brand,
                Money.of(command.price()),
                command.stock(),
                command.description(),
                category
        );

        if (command.applyDiscount()) {
            product.applyDiscount(command.discountPercentage());
        } else {
            product.removeDiscount();
        }
        product.setFeatured(command.featured());
        
        // Save product
        Product savedProduct = productRepository.save(product);
        
        // Publish domain event
        ProductCreatedEvent event = new ProductCreatedEvent(
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getBrand().getName(),
                savedProduct.getPrice().value(),
            savedProduct.getStock(),
                savedProduct.getCategory().getName()
        );
        eventPublisher.publish(event);
        
        return savedProduct;
    }
}
