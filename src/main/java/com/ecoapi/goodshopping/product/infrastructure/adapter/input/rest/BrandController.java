package com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest;

import com.ecoapi.goodshopping.product.application.port.in.*;
import com.ecoapi.goodshopping.product.domain.model.Brand;
import com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.request.BrandRequest;
import com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.response.BrandResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Brand CRUD Operations
 * Brands must be created before products can reference them
 */
@RestController
@RequestMapping("/api/v1/brands")
public class BrandController {
    
    private final AddBrandUseCase addBrandUseCase;
    private final GetAllBrandsUseCase getAllBrandsUseCase;
    private final GetBrandByIdUseCase getBrandByIdUseCase;
    private final UpdateBrandUseCase updateBrandUseCase;
    private final DeleteBrandUseCase deleteBrandUseCase;
    
    public BrandController(AddBrandUseCase addBrandUseCase,
                          GetAllBrandsUseCase getAllBrandsUseCase,
                          GetBrandByIdUseCase getBrandByIdUseCase,
                          UpdateBrandUseCase updateBrandUseCase,
                          DeleteBrandUseCase deleteBrandUseCase) {
        this.addBrandUseCase = addBrandUseCase;
        this.getAllBrandsUseCase = getAllBrandsUseCase;
        this.getBrandByIdUseCase = getBrandByIdUseCase;
        this.updateBrandUseCase = updateBrandUseCase;
        this.deleteBrandUseCase = deleteBrandUseCase;
    }
    
    /**
     * Create a new brand
     * POST /api/v1/brands
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandResponse> addBrand(@Valid @RequestBody BrandRequest request) {
        Brand brand = addBrandUseCase.addBrand(request.name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BrandResponse.fromDomain(brand));
    }
    
    /**
     * Get all brands
     * GET /api/v1/brands
     * Public endpoint - needed for frontend dropdowns
     */
    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        List<BrandResponse> brands = getAllBrandsUseCase.execute().stream()
                .map(BrandResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(brands);
    }
    
    /**
     * Get a brand by ID
     * GET /api/v1/brands/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable Long id) {
        Brand brand = getBrandByIdUseCase.execute(id);
        return ResponseEntity.ok(BrandResponse.fromDomain(brand));
    }
    
    /**
     * Update a brand
     * PUT /api/v1/brands/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandResponse> updateBrand(@PathVariable Long id,
                                                     @Valid @RequestBody BrandRequest request) {
        Brand brand = updateBrandUseCase.updateBrand(id, request.name());
        return ResponseEntity.ok(BrandResponse.fromDomain(brand));
    }
    
    /**
     * Delete a brand
     * DELETE /api/v1/brands/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        deleteBrandUseCase.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
