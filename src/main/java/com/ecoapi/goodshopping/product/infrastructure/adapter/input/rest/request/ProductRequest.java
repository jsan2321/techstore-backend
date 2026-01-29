package com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.request;

import java.math.BigDecimal;
import com.ecoapi.goodshopping.product.application.service.dto.ProductCommand;
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

    @NotNull(message = "Inventory is required") // Best to use Integer wrapper to ensure input presence
    @PositiveOrZero(message = "Inventory cannot be negative") // Inventory implies count, usually >= 0
    Integer inventory, 

    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 150, message = "Description must contain 3-150 characters")
    String description,

    @NotNull(message = "Category ID is required")
    Long categoryId
) {
    public ProductCommand toCommand() {
        return new ProductCommand(name, brandId, price, inventory, description, categoryId);
    }
}