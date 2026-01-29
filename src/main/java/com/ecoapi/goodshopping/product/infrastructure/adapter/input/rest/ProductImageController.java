package com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest;

import com.ecoapi.goodshopping.product.application.port.in.DeleteProductImageUseCase;
import com.ecoapi.goodshopping.product.application.port.in.UploadProductImageUseCase;
import com.ecoapi.goodshopping.product.application.port.out.ImageFile;
import com.ecoapi.goodshopping.product.domain.model.Product;
import com.ecoapi.goodshopping.product.infrastructure.adapter.SpringMultipartFileAdapter;
import com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.response.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for Product Image Operations
 * Handles file upload and deletion for product images
 * Adapts Spring's MultipartFile to domain's ImageFile at the boundary
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductImageController {
    
    private final UploadProductImageUseCase uploadProductImageUseCase;
    private final DeleteProductImageUseCase deleteProductImageUseCase;
    
    public ProductImageController(UploadProductImageUseCase uploadProductImageUseCase,
                                 DeleteProductImageUseCase deleteProductImageUseCase) {
        this.uploadProductImageUseCase = uploadProductImageUseCase;
        this.deleteProductImageUseCase = deleteProductImageUseCase;
    }
    
    /**
     * Upload image for a product
     * POST /api/v1/products/{id}/image
     * Adapts MultipartFile to ImageFile at the infrastructure boundary
     */
    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imageFile) {
        // Adapt Spring's MultipartFile to domain's ImageFile at the boundary
        ImageFile domainImageFile = SpringMultipartFileAdapter.from(imageFile);
        Product product = uploadProductImageUseCase.uploadImage(id, domainImageFile);
        return ResponseEntity.ok(ProductResponse.fromDomain(product));
    }
    
    /**
     * Delete image for a product
     * DELETE /api/v1/products/{id}/image
     */
    @DeleteMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> deleteProductImage(@PathVariable Long id) {
        Product product = deleteProductImageUseCase.deleteImage(id);
        return ResponseEntity.ok(ProductResponse.fromDomain(product));
    }
}
