package com.ecoapi.techstore.cart.application.port.out;

import com.ecoapi.techstore.cart.application.port.out.dto.ProductData;

import java.util.Optional;

/**
 * Output Port for accessing Product context
 * This port defines what Cart needs from Product, without depending on Product's internals
 * 
 * In Hexagonal Architecture, this port is implemented by an adapter in the infrastructure layer
 * that translates between Cart's needs and Product's input ports (use cases)
 */
public interface ProductAccessPort {
    
    /**
     * Get product information by ID
     * 
     * @param productId the product's ID
     * @return Optional containing product data, or empty if not found
     */
    Optional<ProductData> getProductById(Long productId);
    
    /**
     * Check if a product exists and is active
     * 
     * @param productId the product's ID
     * @return true if product exists and is active
     */
    boolean isProductAvailable(Long productId);
}
