package com.ecoapi.techstore.product.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.product.application.port.in.*;
import com.ecoapi.techstore.product.application.port.out.ImageFile;
import com.ecoapi.techstore.product.application.service.dto.AdminProductListReadModel;
import com.ecoapi.techstore.product.application.service.dto.AdminProductSearchCriteria;
import com.ecoapi.techstore.product.domain.model.Product;
import com.ecoapi.techstore.product.infrastructure.adapter.SpringMultipartFileAdapter;
import com.ecoapi.techstore.product.infrastructure.adapter.input.rest.request.ProductRequest;
import com.ecoapi.techstore.product.infrastructure.adapter.input.rest.response.AdminProductDetailResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for Admin Product Operations (Input Adapter)
 * Handles Create, Update, Delete, Image management, Discounts, and Product visibility
 * All operations require ROLE_ADMIN
 */
@RestController
@RequestMapping("${api.prefix}/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Products", description = "Product management endpoints for administrators")
@SecurityRequirement(name = "bearerAuth")
public class AdminProductController {

    private final AddProductUseCase addProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
        private final ActivateProductUseCase activateProductUseCase;
        private final DeactivateProductUseCase deactivateProductUseCase;
    private final UploadProductImageUseCase uploadProductImageUseCase;
    private final DeleteProductImageUseCase deleteProductImageUseCase;
    private final AdminSearchProductsUseCase adminSearchProductsUseCase;
        private final GetProductByIdUseCase getProductByIdUseCase;

    public AdminProductController(AddProductUseCase addProductUseCase,
                                  UpdateProductUseCase updateProductUseCase,
                                  DeleteProductUseCase deleteProductUseCase,
                                  ActivateProductUseCase activateProductUseCase,
                                  DeactivateProductUseCase deactivateProductUseCase,
                                  UploadProductImageUseCase uploadProductImageUseCase,
                                  DeleteProductImageUseCase deleteProductImageUseCase,
                                  AdminSearchProductsUseCase adminSearchProductsUseCase,
                                  GetProductByIdUseCase getProductByIdUseCase) {
        this.addProductUseCase = addProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.activateProductUseCase = activateProductUseCase;
        this.deactivateProductUseCase = deactivateProductUseCase;
        this.uploadProductImageUseCase = uploadProductImageUseCase;
        this.deleteProductImageUseCase = deleteProductImageUseCase;
        this.adminSearchProductsUseCase = adminSearchProductsUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
    }

    @Operation(
            summary = "Search products (Admin)",
            description = "Search and filter products with admin-specific criteria. Can view active/inactive products."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products matching criteria")
    })
    @GetMapping
    public ResponseEntity<PagedResult<AdminProductListReadModel>> searchProducts(
            @Parameter(description = "Filter by category name") @RequestParam(required = false) String category,
            @Parameter(description = "Filter by brand name") @RequestParam(required = false) String brand,
            @Parameter(description = "Minimum price (inclusive)") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price (inclusive)") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Filter by stock availability") @RequestParam(required = false) Boolean inStock,
            @Parameter(description = "Filter by active status") @RequestParam(required = false) Boolean active,
            @Parameter(description = "Filter by featured status") @RequestParam(required = false) Boolean featured,
            @Parameter(description = "Search by name (partial match)") @RequestParam(required = false) String nameContains,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(defaultValue = "asc") String sortDirection) {

        AdminProductSearchCriteria criteria = AdminProductSearchCriteria.builder()
                .category(category)
                .brand(brand)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .inStock(inStock)
                .active(active)
                .featured(featured)
                .nameContains(nameContains)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        PagedResult<AdminProductListReadModel> products = adminSearchProductsUseCase.search(criteria);
        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Get product details (Admin)",
            description = "Retrieves complete product information for admin screens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdminProductDetailResponse> getProductById(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        Product product = getProductByIdUseCase.execute(id);
        return ResponseEntity.ok(AdminProductDetailResponse.fromDomain(product));
    }

    @Operation(
            summary = "Create a new product",
            description = "Creates a new product in the catalog (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = AdminProductDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not authorized")
    })
    @PostMapping
    public ResponseEntity<AdminProductDetailResponse> addProduct(@Valid @RequestBody ProductRequest request) {
        Product product = addProductUseCase.addProduct(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(AdminProductDetailResponse.fromDomain(product));
    }

    @Operation(
            summary = "Update a product",
            description = "Updates an existing product (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(schema = @Schema(implementation = AdminProductDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AdminProductDetailResponse> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        Product product = updateProductUseCase.updateProduct(id, request.toCommand());
        return ResponseEntity.ok(AdminProductDetailResponse.fromDomain(product));
    }

    @Operation(
            summary = "Delete a product",
            description = "Deletes a product from the catalog (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        deleteProductUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Activate a product",
            description = "Activates a product to make it visible in the public catalog (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product activated successfully",
                    content = @Content(schema = @Schema(implementation = AdminProductDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<AdminProductDetailResponse> activateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        Product product = activateProductUseCase.activateProduct(id);
        return ResponseEntity.ok(AdminProductDetailResponse.fromDomain(product));
    }

    @Operation(
            summary = "Deactivate a product",
            description = "Deactivates a product so it is hidden from the public catalog (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deactivated successfully",
                    content = @Content(schema = @Schema(implementation = AdminProductDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<AdminProductDetailResponse> deactivateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        Product product = deactivateProductUseCase.deactivateProduct(id);
        return ResponseEntity.ok(AdminProductDetailResponse.fromDomain(product));
    }

    @Operation(
            summary = "Upload product image",
            description = "Uploads an image for a product (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully",
                    content = @Content(schema = @Schema(implementation = AdminProductDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid image file"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdminProductDetailResponse> uploadProductImage(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Image file") @RequestParam("image") MultipartFile imageFile) {
        ImageFile domainImageFile = SpringMultipartFileAdapter.from(imageFile);
        Product product = uploadProductImageUseCase.uploadImage(id, domainImageFile);
        return ResponseEntity.ok(AdminProductDetailResponse.fromDomain(product));
    }

    @Operation(
            summary = "Delete product image",
            description = "Removes the image from a product (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image deleted successfully",
                    content = @Content(schema = @Schema(implementation = AdminProductDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}/image")
    public ResponseEntity<AdminProductDetailResponse> deleteProductImage(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        Product product = deleteProductImageUseCase.deleteImage(id);
        return ResponseEntity.ok(AdminProductDetailResponse.fromDomain(product));
    }

}