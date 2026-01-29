package com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest;

import com.ecoapi.goodshopping.product.application.port.in.*;
import com.ecoapi.goodshopping.product.domain.model.Category;
import com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.request.CategoryRequest;
import com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.response.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Category CRUD Operations
 * Categories must be created before products can reference them
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    
    private final AddCategoryUseCase addCategoryUseCase;
    private final GetAllCategoriesUseCase getAllCategoriesUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    
    public CategoryController(AddCategoryUseCase addCategoryUseCase,
                             GetAllCategoriesUseCase getAllCategoriesUseCase,
                             GetCategoryByIdUseCase getCategoryByIdUseCase,
                             UpdateCategoryUseCase updateCategoryUseCase,
                             DeleteCategoryUseCase deleteCategoryUseCase) {
        this.addCategoryUseCase = addCategoryUseCase;
        this.getAllCategoriesUseCase = getAllCategoriesUseCase;
        this.getCategoryByIdUseCase = getCategoryByIdUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }
    
    /**
     * Create a new category
     * POST /api/v1/categories
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody CategoryRequest request) {
        Category category = addCategoryUseCase.addCategory(request.name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryResponse.fromDomain(category));
    }
    
    /**
     * Get all categories
     * GET /api/v1/categories
     * Public endpoint - needed for frontend dropdowns
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = getAllCategoriesUseCase.execute().stream()
                .map(CategoryResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }
    
    /**
     * Get a category by ID
     * GET /api/v1/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        Category category = getCategoryByIdUseCase.execute(id);
        return ResponseEntity.ok(CategoryResponse.fromDomain(category));
    }
    
    /**
     * Update a category
     * PUT /api/v1/categories/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                           @Valid @RequestBody CategoryRequest request) {
        Category category = updateCategoryUseCase.updateCategory(id, request.name());
        return ResponseEntity.ok(CategoryResponse.fromDomain(category));
    }
    
    /**
     * Delete a category
     * DELETE /api/v1/categories/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        deleteCategoryUseCase.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
