package com.ecoapi.techstore.product.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.product.application.port.in.*;
import com.ecoapi.techstore.product.domain.model.Category;
import com.ecoapi.techstore.product.infrastructure.adapter.input.rest.request.CategoryRequest;
import com.ecoapi.techstore.product.infrastructure.adapter.input.rest.response.CategoryResponse;

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
 * REST Controller for Admin Category Operations (Input Adapter)
 * Handles Create, Update, Delete operations
 * All operations require ROLE_ADMIN
 */
@RestController
@RequestMapping("${api.prefix}/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Categories", description = "Category management endpoints for administrators")
@SecurityRequirement(name = "bearerAuth")
public class AdminCategoryController {
    
    private final AddCategoryUseCase addCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    
    public AdminCategoryController(AddCategoryUseCase addCategoryUseCase,
                                   UpdateCategoryUseCase updateCategoryUseCase,
                                   DeleteCategoryUseCase deleteCategoryUseCase) {
        this.addCategoryUseCase = addCategoryUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }
    
    @Operation(
            summary = "Create a new category",
            description = "Creates a new product category (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Category already exists")
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody CategoryRequest request) {
        Category category = addCategoryUseCase.addCategory(request.name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryResponse.fromDomain(category));
    }
    
    @Operation(
            summary = "Update a category",
            description = "Updates an existing category (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "Category ID") @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        Category category = updateCategoryUseCase.updateCategory(id, request.name());
        return ResponseEntity.ok(CategoryResponse.fromDomain(category));
    }
    
    @Operation(
            summary = "Delete a category",
            description = "Deletes a category from the system (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID") @PathVariable Long id) {
        deleteCategoryUseCase.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}