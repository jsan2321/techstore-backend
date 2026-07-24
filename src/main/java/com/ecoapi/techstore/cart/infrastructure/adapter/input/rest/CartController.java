package com.ecoapi.techstore.cart.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.cart.application.port.in.*;
import com.ecoapi.techstore.cart.application.port.out.ProductAccessPort;
import com.ecoapi.techstore.cart.application.port.out.dto.ProductData;
import com.ecoapi.techstore.cart.application.service.dto.*;
import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.infrastructure.adapter.input.rest.request.AddItemRequest;
import com.ecoapi.techstore.cart.infrastructure.adapter.input.rest.request.UpdateQuantityRequest;
import com.ecoapi.techstore.cart.infrastructure.adapter.input.rest.response.CartResponse;
import com.ecoapi.techstore.common.infrastructure.security.util.SecurityContextUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for "My Cart" operations (Input Adapter)
 * User-centric cart management - all operations are for the authenticated user's cart
 * No cart IDs in URLs - cart is identified by the authenticated user
 */
@RestController
@RequestMapping("${api.prefix}/cart")
@Tag(name = "Shopping Cart", description = "Shopping cart management for authenticated users")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final AddItemToCartUseCase addItemToCartUseCase;
    private final RemoveItemFromCartUseCase removeItemFromCartUseCase;
    private final UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;
    private final GetCartUseCase getCartUseCase;
    private final ClearCartUseCase clearCartUseCase;
        private final ProductAccessPort productAccessPort;

    public CartController(AddItemToCartUseCase addItemToCartUseCase,
                         RemoveItemFromCartUseCase removeItemFromCartUseCase,
                         UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase,
                         GetCartUseCase getCartUseCase,
                                                 ClearCartUseCase clearCartUseCase,
                                                 ProductAccessPort productAccessPort) {
        this.addItemToCartUseCase = addItemToCartUseCase;
        this.removeItemFromCartUseCase = removeItemFromCartUseCase;
        this.updateCartItemQuantityUseCase = updateCartItemQuantityUseCase;
        this.getCartUseCase = getCartUseCase;
        this.clearCartUseCase = clearCartUseCase;
                this.productAccessPort = productAccessPort;
    }

        private Map<Long, ProductData> buildProductDataMap(Cart cart) {
                Map<Long, ProductData> productDataById = new HashMap<>();

                cart.getItems().forEach(item -> {
                        Long productId = item.getProductId().value();
                        productDataById.computeIfAbsent(productId,
                                        id -> productAccessPort.getProductById(id).orElse(null));
                });

                return productDataById;
        }

    @Operation(
            summary = "Get my cart",
            description = "Retrieves the current user's shopping cart. Creates a new cart if one doesn't exist."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> getMyCart() {
        Long userId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));

        Cart cart = getCartUseCase.getCart(GetCartQuery.forUser(userId));
        return ResponseEntity.ok(CartResponse.fromDomain(cart, buildProductDataMap(cart)));
    }

    @Operation(
            summary = "Add item to cart",
            description = "Adds a product to the user's shopping cart with the specified quantity"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added successfully",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> addItem(@Valid @RequestBody AddItemRequest request) {
        Long userId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));

        // Add item to the user's cart using command object
        AddItemCommand command = AddItemCommand.forUser(userId, request.productId(), request.quantity());
        Cart updatedCart = addItemToCartUseCase.addItem(command);
        return ResponseEntity.ok(CartResponse.fromDomain(updatedCart, buildProductDataMap(updatedCart)));
    }

    @Operation(
            summary = "Remove item from cart",
            description = "Removes a product from the user's shopping cart"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Product not in cart")
    })
    @DeleteMapping("/items/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> removeItem(
            @Parameter(description = "Product ID to remove") @PathVariable Long productId) {
        Long userId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));

        // Remove item from the user's cart using command object
        RemoveItemCommand command = RemoveItemCommand.forUser(userId, productId);
        Cart updatedCart = removeItemFromCartUseCase.removeItem(command);
        return ResponseEntity.ok(CartResponse.fromDomain(updatedCart, buildProductDataMap(updatedCart)));
    }

    @Operation(
            summary = "Update item quantity",
            description = "Updates the quantity of a product in the user's shopping cart"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quantity updated successfully",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid quantity"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "404", description = "Product not in cart")
    })
    @PutMapping("/items/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @Parameter(description = "Product ID") @PathVariable Long productId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        Long userId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));

        // Update item quantity in the user's cart using command object
        UpdateItemQuantityCommand command = UpdateItemQuantityCommand.forUser(userId, productId, request.quantity());
        Cart updatedCart = updateCartItemQuantityUseCase.updateItemQuantity(command);
        return ResponseEntity.ok(CartResponse.fromDomain(updatedCart, buildProductDataMap(updatedCart)));
    }

    @Operation(
            summary = "Clear cart",
            description = "Removes all items from the user's shopping cart"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart cleared successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> clearCart() {
        Long userId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));
    
        // Clear the user's cart using command object
        ClearCartCommand command = ClearCartCommand.forUser(userId);
        clearCartUseCase.clearCart(command);
        return ResponseEntity.noContent().build();
    }
}