package com.ecoapi.techstore.product.infrastructure.adapter.input.rest.request;

import java.math.BigDecimal;
import com.ecoapi.techstore.product.application.service.dto.ProductCommand;
import jakarta.validation.constraints.*;

public record ProductRequest(
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 150, message = "Product name must contain 3-150 characters")
    String name,

    @NotNull(message = "Brand ID is required")
    Long brandId,

    @NotNull(message = "Price is required") // Use NotNull for objects
    @Positive(message = "Price must be positive") // Checks value > 0
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 integer digits and 2 decimal digits")
    BigDecimal price,

    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock cannot be negative")
    Integer stock,

    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 150, message = "Description must contain 3-150 characters")
    String description,

    @NotNull(message = "Category ID is required")
    Long categoryId,

    Boolean applyDiscount,

    @Min(value = 1, message = "Discount percentage must be at least 1")
    @Max(value = 99, message = "Discount percentage cannot exceed 99")
    Integer discountPercentage,

    Boolean featured
) {
    public ProductRequest {
        if (Boolean.TRUE.equals(applyDiscount) && discountPercentage == null) {
            throw new IllegalArgumentException("discountPercentage is required when applyDiscount is true");
        }
    }

    public ProductCommand toCommand() {
        return new ProductCommand(
                name,
                brandId,
                price,
                stock,
                description,
                categoryId,
                Boolean.TRUE.equals(applyDiscount),
                discountPercentage,
                Boolean.TRUE.equals(featured)
        );
    }
}