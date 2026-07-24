package com.ecoapi.techstore.product.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.product.application.port.in.*;
import com.ecoapi.techstore.product.domain.model.Brand;
import com.ecoapi.techstore.product.infrastructure.adapter.input.rest.request.BrandRequest;
import com.ecoapi.techstore.product.infrastructure.adapter.input.rest.response.BrandResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Admin Brand Operations (Input Adapter)
 * Handles Create, Update, Delete operations
 * All operations require ROLE_ADMIN
 */
@RestController
@RequestMapping("${api.prefix}/admin/brands")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Brands", description = "Brand management endpoints for administrators")
@SecurityRequirement(name = "bearerAuth")
public class AdminBrandController {
    
    private final AddBrandUseCase addBrandUseCase;
    private final UpdateBrandUseCase updateBrandUseCase;
    private final DeleteBrandUseCase deleteBrandUseCase;
    
    public AdminBrandController(AddBrandUseCase addBrandUseCase,
                                UpdateBrandUseCase updateBrandUseCase,
                                DeleteBrandUseCase deleteBrandUseCase) {
        this.addBrandUseCase = addBrandUseCase;
        this.updateBrandUseCase = updateBrandUseCase;
        this.deleteBrandUseCase = deleteBrandUseCase;
    }
    
    @Operation(
            summary = "Create a new brand",
            description = "Creates a new product brand (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Brand created successfully",
                    content = @Content(schema = @Schema(implementation = BrandResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Brand already exists")
    })
    @PostMapping
    public ResponseEntity<BrandResponse> addBrand(@Valid @RequestBody BrandRequest request) {
        Brand brand = addBrandUseCase.addBrand(request.name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BrandResponse.fromDomain(brand));
    }
    
    @Operation(
            summary = "Update a brand",
            description = "Updates an existing brand (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Brand updated successfully",
                    content = @Content(schema = @Schema(implementation = BrandResponse.class))),
            @ApiResponse(responseCode = "404", description = "Brand not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BrandResponse> updateBrand(
            @Parameter(description = "Brand ID") @PathVariable Long id,
            @Valid @RequestBody BrandRequest request) {
        Brand brand = updateBrandUseCase.updateBrand(id, request.name());
        return ResponseEntity.ok(BrandResponse.fromDomain(brand));
    }
    
    @Operation(
            summary = "Delete a brand",
            description = "Deletes a brand from the system (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Brand deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Brand not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(
            @Parameter(description = "Brand ID") @PathVariable Long id) {
        deleteBrandUseCase.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}