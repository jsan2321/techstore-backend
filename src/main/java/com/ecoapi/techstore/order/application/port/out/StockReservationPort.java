package com.ecoapi.techstore.order.application.port.out;

/**
 * Output port for stock reservation operations
 * This port allows the Order context to manage inventory without direct coupling to the Product context
 * 
 * Uses primitive types (Long) instead of ProductId to avoid cross-context domain model coupling
 */
public interface StockReservationPort {
    
    /**
     * Check if sufficient stock is available for a product
     * @param productId The product ID
     * @param quantity The required quantity
     * @return true if sufficient stock is available
     */
    boolean hasAvailableStock(Long productId, int quantity);
    
    /**
     * Reserve stock for a product (reduce available inventory)
     * @param productId The product ID
     * @param quantity The quantity to reserve
     * @throws InsufficientStockException if stock is not available
     */
    void reserveStock(Long productId, int quantity);
    
    /**
     * Release previously reserved stock (restore inventory on order cancellation)
     * @param productId The product ID
     * @param quantity The quantity to release
     */
    void releaseStock(Long productId, int quantity);
    
    /**
     * Get the product name for error messages
     * @param productId The product ID
     * @return The product name
     */
    String getProductName(Long productId);
}
