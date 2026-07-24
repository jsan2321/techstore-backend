package com.ecoapi.techstore.order.infrastructure.adapter.output.product;

import com.ecoapi.techstore.order.application.port.out.StockReservationPort;
import com.ecoapi.techstore.order.domain.exception.InsufficientStockException;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.product.domain.model.Product;
import com.ecoapi.techstore.common.domain.valueobjects.ProductId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Infrastructure adapter for stock reservation
 * Implements the StockReservationPort by delegating to the Product context
 * 
 * This adapter translates between Order's primitive types and Product's domain types,
 * acting as an Anti-Corruption Layer between bounded contexts.
 * 
 * Note: Uses Order's own InsufficientStockException, not Product's,
 * to maintain proper context isolation.
 */
public class StockReservationAdapter implements StockReservationPort {
    
    private static final Logger logger = LoggerFactory.getLogger(StockReservationAdapter.class);
    
    private final ProductRepositoryPort productRepository;
    
    public StockReservationAdapter(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public boolean hasAvailableStock(Long productId, int quantity) {
        try {
            ProductId pid = ProductId.of(productId);
            Product product = productRepository.findById(pid)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found: " + productId));

            if (!product.isActive()) {
                return false;
            }
            
            return product.hasStock(quantity);
        } catch (Exception e) {
            logger.error("Error checking stock for product {}: {}", productId, e.getMessage());
            return false;
        }
    }
    
    @Override
    public void reserveStock(Long productId, int quantity) {
        ProductId pid = ProductId.of(productId);
        Product product = productRepository.findById(pid)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + productId));

        if (!product.isActive()) {
            throw new InsufficientStockException(
                "Product is not available: " + product.getName());
        }
        
        if (!product.hasStock(quantity)) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName() +
                    ". Available: " + product.getStock() + ", Requested: " + quantity);
        }
        
        // Reduce stock
        product.reduceStock(quantity);
        productRepository.save(product);
        
        logger.info("Reserved {} units of product {} (ID: {}). Remaining stock: {}", 
            quantity, product.getName(), productId, product.getStock());
    }
    
    @Override
    public void releaseStock(Long productId, int quantity) {
        try {
            ProductId pid = ProductId.of(productId);
            Product product = productRepository.findById(pid)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found: " + productId));
            
            // Add stock back
            product.addStock(quantity);
            productRepository.save(product);
            
            logger.info("Released {} units of product {} (ID: {}). New stock: {}", 
                    quantity, product.getName(), productId, product.getStock());
        } catch (Exception e) {
            logger.error("Error releasing stock for product {}: {}", productId, e.getMessage());
            // Log but don't throw - stock release failures shouldn't break cancellation
        }
    }
    
    @Override
    public String getProductName(Long productId) {
        ProductId pid = ProductId.of(productId);
        return productRepository.findById(pid)
                .map(Product::getName)
                .orElse("Unknown Product");
    }
}
