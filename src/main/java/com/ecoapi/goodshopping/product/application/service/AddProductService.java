package com.ecoapi.goodshopping.product.application.service;

import com.ecoapi.goodshopping.product.application.port.in.AddProductUseCase;
import com.ecoapi.goodshopping.product.application.port.out.CategoryRepositoryPort;
import com.ecoapi.goodshopping.product.application.port.out.ProductEventPublisherPort;
import com.ecoapi.goodshopping.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.goodshopping.product.application.service.dto.ProductCommand;
import com.ecoapi.goodshopping.product.domain.events.ProductCreatedEvent;
import com.ecoapi.goodshopping.product.domain.exception.ProductAlreadyExistsException;
import com.ecoapi.goodshopping.product.domain.model.Brand;
import com.ecoapi.goodshopping.product.domain.model.Category;
import com.ecoapi.goodshopping.product.domain.model.Money;
import com.ecoapi.goodshopping.product.domain.model.Product;

/**
 * Application Service for adding new products
 * Single Responsibility: Handle product creation business logic
 */
public class AddProductService implements AddProductUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final ProductEventPublisherPort eventPublisher;
    
    public AddProductService(ProductRepositoryPort productRepository,
                            CategoryRepositoryPort categoryRepository,
                            ProductEventPublisherPort eventPublisher) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Product addProduct(ProductCommand command) {
        // Check if product already exists
        if (productRepository.existsByNameAndBrand(command.name(), command.brand())) {
            throw new ProductAlreadyExistsException(
                command.brand() + " " + command.name() + " already exists");
        }
        
        // Get or create category
        Category category = categoryRepository.findByName(command.categoryName())
                .orElseGet(() -> categoryRepository.save(new Category(command.categoryName())));
        
        // Create domain product using factory method
        Product product = Product.create(
                command.name(),
                Brand.of(command.brand()),
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
                savedProduct.getBrand().value(),
                savedProduct.getPrice().value(),
                savedProduct.getInventory(),
                savedProduct.getCategory().getName()
        );
        eventPublisher.publish(event);
        
        return savedProduct;
    }
}
