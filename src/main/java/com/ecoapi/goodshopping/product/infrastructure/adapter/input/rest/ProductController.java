package com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest;

import com.ecoapi.goodshopping.product.application.port.in.*;
import com.ecoapi.goodshopping.product.application.service.dto.ProductCommand;
import com.ecoapi.goodshopping.product.application.service.dto.UpdateProductCommand;
import com.ecoapi.goodshopping.product.domain.model.Product;
import com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.request.ProductRequest;
import com.ecoapi.goodshopping.product.infrastructure.adapter.input.rest.response.ProductResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Product operations
 * Input adapter that translates HTTP requests to use case calls
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    
    private final AddProductUseCase addProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final GetAllProductsUseCase getAllProductsUseCase;
    private final GetProductsByCategoryUseCase getProductsByCategoryUseCase;
    private final GetProductsByBrandUseCase getProductsByBrandUseCase;
    private final SearchProductsUseCase searchProductsUseCase;
    private final UploadProductImageUseCase uploadProductImageUseCase;
    private final DeleteProductImageUseCase deleteProductImageUseCase;
    
    public ProductController(AddProductUseCase addProductUseCase,
                           UpdateProductUseCase updateProductUseCase,
                           DeleteProductUseCase deleteProductUseCase,
                           GetProductByIdUseCase getProductByIdUseCase,
                           GetAllProductsUseCase getAllProductsUseCase,
                           GetProductsByCategoryUseCase getProductsByCategoryUseCase,
                           GetProductsByBrandUseCase getProductsByBrandUseCase,
                           SearchProductsUseCase searchProductsUseCase,
                           UploadProductImageUseCase uploadProductImageUseCase,
                           DeleteProductImageUseCase deleteProductImageUseCase) {
        this.addProductUseCase = addProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.getAllProductsUseCase = getAllProductsUseCase;
        this.getProductsByCategoryUseCase = getProductsByCategoryUseCase;
        this.getProductsByBrandUseCase = getProductsByBrandUseCase;
        this.searchProductsUseCase = searchProductsUseCase;
        this.uploadProductImageUseCase = uploadProductImageUseCase;
        this.deleteProductImageUseCase = deleteProductImageUseCase;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
        Product product = addProductUseCase.addProduct(request.toCommand());
        // ProductCommand command = new ProductCommand(
        //         request.name(),
        //         request.brand(),
        //         request.price(),
        //         request.inventory(),
        //         request.description(),
        //         request.categoryName()
        // );
        
        // Product product = addProductUseCase.addProduct(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductResponse.fromDomain(product));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @Valid @RequestBody ProductRequest request) {
        UpdateProductCommand command = new UpdateProductCommand(
                request.name(),
                request.brand(),
                request.price(),
                request.inventory(),
                request.description(),
                request.categoryName()
        );
        
        Product product = updateProductUseCase.updateProduct(id, command);
        return ResponseEntity.ok(ProductResponse.fromDomain(product));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = getProductByIdUseCase.execute(id);
        return ResponseEntity.ok(ProductResponse.fromDomain(product));
    }
    
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = getAllProductsUseCase.execute();
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = getProductsByCategoryUseCase.execute(category);
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<ProductResponse>> getProductsByBrand(@PathVariable String brand) {
        List<Product> products = getProductsByBrandUseCase.execute(brand);
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/category/{category}/brand/{brand}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategoryAndBrand(
            @PathVariable String category, 
            @PathVariable String brand) {
        List<Product> products = searchProductsUseCase.byCategoryAndBrand(category, brand);
        List<ProductResponse> response = products.stream()
                .map(ProductResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        deleteProductUseCase.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imageFile) {
        Product product = uploadProductImageUseCase.uploadImage(id, imageFile);
        return ResponseEntity.ok(ProductResponse.fromDomain(product));
    }
    
    @DeleteMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> deleteProductImage(@PathVariable Long id) {
        Product product = deleteProductImageUseCase.deleteImage(id);
        return ResponseEntity.ok(ProductResponse.fromDomain(product));
    }
}
