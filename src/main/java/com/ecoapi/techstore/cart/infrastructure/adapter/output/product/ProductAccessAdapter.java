package com.ecoapi.techstore.cart.infrastructure.adapter.output.product;

import com.ecoapi.techstore.cart.application.port.out.ProductAccessPort;
import com.ecoapi.techstore.cart.application.port.out.dto.ProductData;
import com.ecoapi.techstore.product.application.port.in.GetProductByIdUseCase;
import com.ecoapi.techstore.product.domain.model.Product;
import com.ecoapi.techstore.common.domain.valueobjects.Money;

import java.math.BigDecimal;

import java.util.Optional;

/**
 * Infrastructure Adapter for Product context access
 * 
 * This adapter implements Cart's ProductAccessPort by delegating to Product's input ports (use cases).
 * It acts as an Anti-Corruption Layer (ACL), translating between Cart's DTOs and Product's domain model.
 * 
 * The adapter lives in Cart's infrastructure layer because:
 * 1. It implements a Cart port (ProductAccessPort)
 * 2. It knows about Product's use cases (infrastructure concern)
 * 3. It translates between contexts (ACL pattern)
 */
public class ProductAccessAdapter implements ProductAccessPort {
    
    private final GetProductByIdUseCase getProductByIdUseCase;
    
    public ProductAccessAdapter(GetProductByIdUseCase getProductByIdUseCase) {
        this.getProductByIdUseCase = getProductByIdUseCase;
    }
    
    @Override
    public Optional<ProductData> getProductById(Long productId) {
        try {
            // Call Product's input port (use case)
            Product product = getProductByIdUseCase.execute(productId);

            if (!product.isActive()) {
                return Optional.empty();
            }
            
            // Translate Product's domain model to Cart's DTO (Anti-Corruption Layer)
            return Optional.of(toProductData(product));
        } catch (Exception e) {
            // Product not found or other error - return empty
            return Optional.empty();
        }
    }
    
    @Override
    public boolean isProductAvailable(Long productId) {
        try {
            Product product = getProductByIdUseCase.execute(productId);
            return product != null && product.isActive();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Translate Product domain model to Cart's ProductData DTO
     * This is the Anti-Corruption Layer translation
     */
    private ProductData toProductData(Product product) {
        Money originalPrice = product.getPrice();
        Money effectivePrice = originalPrice;
        Integer discountPercentage = product.getDiscountPercentage();

        if (discountPercentage != null && discountPercentage > 0) {
            BigDecimal discountAmount = originalPrice.value()
                    .multiply(BigDecimal.valueOf(discountPercentage))
                    .divide(BigDecimal.valueOf(100));
            effectivePrice = Money.of(originalPrice.value().subtract(discountAmount));
        }

        return new ProductData(
                product.getId().value(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl() != null ? product.getImageUrl().value() : null,
                originalPrice,
                effectivePrice,
                discountPercentage,
                product.getStock(),
                product.isActive()
        );
    }
}
