package com.ecoapi.techstore.product.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.product.application.port.in.GetProductByIdUseCase;
import com.ecoapi.techstore.product.application.port.in.SearchProductsByCriteriaUseCase;
import com.ecoapi.techstore.product.application.service.dto.ProductReadModel;
import com.ecoapi.techstore.product.domain.model.Product;
import com.ecoapi.techstore.product.application.service.dto.ProductSearchCriteria;
import com.ecoapi.techstore.product.infrastructure.adapter.input.rest.response.ProductResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Public Product Operations (Input Adapter)
 * Handles read-only operations accessible to all users
 * Admin operations are in AdminProductController
 */
@RestController
@RequestMapping("${api.prefix}/products")
@Tag(name = "Products", description = "Product catalog browsing endpoints")
public class ProductController {
    
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final SearchProductsByCriteriaUseCase searchProductsByCriteriaUseCase;
    
    public ProductController(GetProductByIdUseCase getProductByIdUseCase,
                             SearchProductsByCriteriaUseCase searchProductsByCriteriaUseCase) {
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.searchProductsByCriteriaUseCase = searchProductsByCriteriaUseCase;
    }
    
    @Operation(
            summary = "Get product by ID",
            description = "Retrieves detailed information about a specific product"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id) {
        Product product = getProductByIdUseCase.execute(id);
        return ResponseEntity.ok(ProductResponse.fromDomain(product));
    }

    @Operation(
            summary = "Search products",
            description = "Search and filter products with optional criteria. All parameters are optional and can be combined. Only active products are returned."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products matching criteria")
    })
    @GetMapping
    public ResponseEntity<PagedResult<ProductReadModel>> searchProducts(
            @Parameter(description = "Search query for name") @RequestParam(required = false) String q,
            @Parameter(description = "Filter by category name") @RequestParam(required = false) String category,
            @Parameter(description = "Filter by brand name") @RequestParam(required = false) String brand,
            @Parameter(description = "Minimum price (inclusive)") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price (inclusive)") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Filter by stock availability") @RequestParam(required = false) Boolean inStock,
            @Parameter(description = "Filter by featured status") @RequestParam(required = false) Boolean featured,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(defaultValue = "asc") String sortDirection) {
        
        // Build search criteria from query parameters
        // Note: active is always true for public endpoint - only show active products to customers
        ProductSearchCriteria criteria = ProductSearchCriteria.builder()
                .nameContains(q)
                .category(category)
                .brand(brand)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .inStock(inStock)
                .featured(featured)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        // Execute search with criteria
        PagedResult<ProductReadModel> products = searchProductsByCriteriaUseCase.search(criteria);
        
        return ResponseEntity.ok(products);
    }
}
