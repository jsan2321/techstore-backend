package com.ecoapi.techstore.product.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.product.application.port.in.GetAllCategoriesUseCase;
import com.ecoapi.techstore.product.application.port.in.GetCategoryByIdUseCase;
import com.ecoapi.techstore.product.domain.model.Category;
import com.ecoapi.techstore.product.infrastructure.adapter.input.rest.response.CategoryResponse;

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
 * REST Controller for Public Category Operations (Input Adapter)
 * Handles read-only operations accessible to all users
 * Admin operations are in AdminCategoryController
 */
@RestController
@RequestMapping("${api.prefix}/categories")
@Tag(name = "Categories", description = "Product category browsing endpoints")
public class CategoryController {
    
    private final GetAllCategoriesUseCase getAllCategoriesUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    
    public CategoryController(GetAllCategoriesUseCase getAllCategoriesUseCase,
                             GetCategoryByIdUseCase getCategoryByIdUseCase) {
        this.getAllCategoriesUseCase = getAllCategoriesUseCase;
        this.getCategoryByIdUseCase = getCategoryByIdUseCase;
    }
    
    @Operation(
            summary = "Get all categories",
            description = "Retrieves a list of all product categories"
    )
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = getAllCategoriesUseCase.execute().stream()
                .map(CategoryResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }
    
    @Operation(
            summary = "Get category by ID",
            description = "Retrieves a specific category by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "Category ID") @PathVariable Long id) {
        Category category = getCategoryByIdUseCase.execute(id);
        return ResponseEntity.ok(CategoryResponse.fromDomain(category));
    }
}
