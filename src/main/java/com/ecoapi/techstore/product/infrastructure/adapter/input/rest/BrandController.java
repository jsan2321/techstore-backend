package com.ecoapi.techstore.product.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.product.application.port.in.GetAllBrandsUseCase;
import com.ecoapi.techstore.product.application.port.in.GetBrandByIdUseCase;
import com.ecoapi.techstore.product.domain.model.Brand;
import com.ecoapi.techstore.product.infrastructure.adapter.input.rest.response.BrandResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Public Brand Operations (Input Adapter)
 * Handles read-only operations accessible to all users
 * Admin operations are in AdminBrandController
 */
@RestController
@RequestMapping("${api.prefix}/brands")
@Tag(name = "Brands", description = "Product brand browsing endpoints")
public class BrandController {
    
    private final GetAllBrandsUseCase getAllBrandsUseCase;
    private final GetBrandByIdUseCase getBrandByIdUseCase;
    
    public BrandController(GetAllBrandsUseCase getAllBrandsUseCase,
                          GetBrandByIdUseCase getBrandByIdUseCase) {
        this.getAllBrandsUseCase = getAllBrandsUseCase;
        this.getBrandByIdUseCase = getBrandByIdUseCase;
    }
    
    @Operation(
            summary = "Get all brands",
            description = "Retrieves a list of all product brands"
    )
    @ApiResponse(responseCode = "200", description = "Brands retrieved successfully")
    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        List<BrandResponse> brands = getAllBrandsUseCase.execute().stream()
                .map(BrandResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(brands);
    }
    
    @Operation(
            summary = "Get brand by ID",
            description = "Retrieves a specific brand by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand found",
                    content = @Content(schema = @Schema(implementation = BrandResponse.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(
            @Parameter(description = "Brand ID") @PathVariable Long id) {
        Brand brand = getBrandByIdUseCase.execute(id);
        return ResponseEntity.ok(BrandResponse.fromDomain(brand));
    }
}
