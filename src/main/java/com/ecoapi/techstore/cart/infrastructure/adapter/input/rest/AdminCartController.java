package com.ecoapi.techstore.cart.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.cart.application.port.in.*;
import com.ecoapi.techstore.cart.application.service.dto.*;
import com.ecoapi.techstore.cart.domain.model.Cart;
import com.ecoapi.techstore.cart.infrastructure.adapter.input.rest.response.CartResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



/**
 * REST Controller for Administrative Cart Management (Input Adapter)
 * Allows admins to manage any user's cart
 * All operations require ROLE_ADMIN
 */
@RestController
@RequestMapping("${api.prefix}/admin/carts")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Carts", description = "Cart management endpoints for administrators")
@SecurityRequirement(name = "bearerAuth")
public class AdminCartController {

    private final GetCartUseCase getCartUseCase;
    private final ClearCartUseCase clearCartUseCase;
    private final RemoveItemFromCartUseCase removeItemFromCartUseCase;

    public AdminCartController(GetCartUseCase getCartUseCase,
                              ClearCartUseCase clearCartUseCase,
                              RemoveItemFromCartUseCase removeItemFromCartUseCase) {
        this.getCartUseCase = getCartUseCase;
        this.clearCartUseCase = clearCartUseCase;
        this.removeItemFromCartUseCase = removeItemFromCartUseCase;
    }

    @Operation(
            summary = "Get cart by ID",
            description = "Retrieves a cart by its ID (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart found",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponse> getCartById(
            @Parameter(description = "Cart ID") @PathVariable Long cartId) {
        Cart cart = getCartUseCase.getCart(GetCartQuery.forCart(cartId));
        return ResponseEntity.ok(CartResponse.fromDomain(cart));
    }

    @Operation(
            summary = "Get user's cart",
            description = "Retrieves a user's cart by user ID. Creates a new cart if one doesn't exist. (Admin only)"
    )
    @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
            content = @Content(schema = @Schema(implementation = CartResponse.class)))
    @GetMapping("/search/by-user")
    public ResponseEntity<CartResponse> getCartByUserId(
            @Parameter(description = "User ID") @RequestParam Long userId) {
        Cart cart = getCartUseCase.getCart(GetCartQuery.forUser(userId));
        return ResponseEntity.ok(CartResponse.fromDomain(cart));
    }

    @Operation(
            summary = "Clear a cart",
            description = "Removes all items from a cart (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart cleared successfully"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> clearCart(
            @Parameter(description = "Cart ID") @PathVariable Long cartId) {
        clearCartUseCase.clearCart(ClearCartCommand.forCart(cartId));
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remove item from cart",
            description = "Removes a specific product from a cart - useful for customer support scenarios (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cart or product not found")
    })
    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @Parameter(description = "Cart ID") @PathVariable Long cartId,
            @Parameter(description = "Product ID") @PathVariable Long productId) {
        RemoveItemCommand command = RemoveItemCommand.forCart(cartId, productId);
        Cart cart = removeItemFromCartUseCase.removeItem(command);
        return ResponseEntity.ok(CartResponse.fromDomain(cart));
    }
}
