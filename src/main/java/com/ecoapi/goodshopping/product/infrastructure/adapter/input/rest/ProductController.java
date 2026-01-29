package com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest;

import com.ecoapi.goodshopping.product.application.port.in.*;
import com.ecoapi.goodshopping.product.domain.model.Product;
import com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.request.ProductRequest;
import com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.response.ProductResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Product CRUD Operations
 * Handles Create, Read (by ID), Update, and Delete operations
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    private final AddProductUseCase addProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    
    public ProductController(AddProductUseCase addProductUseCase,
                           UpdateProductUseCase updateProductUseCase,
                           DeleteProductUseCase deleteProductUseCase,
                           GetProductByIdUseCase getProductByIdUseCase) {
        this.addProductUseCase = addProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
    }
    
    /**
     * Create a new product
     * POST /api/v1/products
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
        Product product = addProductUseCase.addProduct(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductResponse.fromDomain(product));
    }
    
    /**
     * Update an existing product
     * PUT /api/v1/products/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @Valid @RequestBody ProductRequest request) {
        Product product = updateProductUseCase.updateProduct(id, request.toCommand());
        return ResponseEntity.ok(ProductResponse.fromDomain(product));
    }
    
    /**
     * Get a product by ID
     * GET /api/v1/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = getProductByIdUseCase.execute(id);
        return ResponseEntity.ok(ProductResponse.fromDomain(product));
    }
    
    /**
     * Delete a product
     * DELETE /api/v1/products/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        deleteProductUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
