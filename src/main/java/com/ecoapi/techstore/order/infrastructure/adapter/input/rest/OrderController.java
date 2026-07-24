package com.ecoapi.techstore.order.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.common.infrastructure.security.util.SecurityContextUtil;
import com.ecoapi.techstore.order.application.port.in.CapturePayPalPaymentUseCase;
import com.ecoapi.techstore.order.application.port.in.GetOrderUseCase;
import com.ecoapi.techstore.order.application.port.in.InitiatePayPalPaymentUseCase;
import com.ecoapi.techstore.order.application.port.in.PlaceOrderUseCase;
import com.ecoapi.techstore.order.application.port.in.UpdateOrderStatusUseCase;
import com.ecoapi.techstore.order.application.service.dto.CapturePayPalPaymentCommand;
import com.ecoapi.techstore.order.application.service.dto.GetOrderQuery;
import com.ecoapi.techstore.order.application.service.dto.InitiatePayPalPaymentCommand;
import com.ecoapi.techstore.order.application.service.dto.PlaceOrderCommand;
import com.ecoapi.techstore.order.application.service.dto.UpdateOrderStatusCommand;
import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.infrastructure.adapter.input.rest.request.CapturePayPalPaymentRequest;
import com.ecoapi.techstore.order.infrastructure.adapter.input.rest.request.CancelOrderRequest;
import com.ecoapi.techstore.order.infrastructure.adapter.input.rest.request.PlaceOrderRequest;
import com.ecoapi.techstore.order.infrastructure.adapter.input.rest.request.ShippingAddressRequest;
import com.ecoapi.techstore.order.infrastructure.adapter.input.rest.response.OrderResponse;
import com.ecoapi.techstore.order.infrastructure.adapter.input.rest.response.PayPalPaymentInitiationResponse;

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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Order operations (Input Adapter)
 * User-centric order management - operations use the authenticated user
 * Admin operations are in AdminOrderController
 */
@RestController
@RequestMapping("${api.prefix}/orders")
@Tag(name = "Orders", description = "Order management endpoints for authenticated users")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
    
    private final PlaceOrderUseCase placeOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
        private final InitiatePayPalPaymentUseCase initiatePayPalPaymentUseCase;
        private final CapturePayPalPaymentUseCase capturePayPalPaymentUseCase;
    
    public OrderController(PlaceOrderUseCase placeOrderUseCase,
                          GetOrderUseCase getOrderUseCase,
                                                  UpdateOrderStatusUseCase updateOrderStatusUseCase,
                                                  InitiatePayPalPaymentUseCase initiatePayPalPaymentUseCase,
                                                  CapturePayPalPaymentUseCase capturePayPalPaymentUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
                this.initiatePayPalPaymentUseCase = initiatePayPalPaymentUseCase;
                this.capturePayPalPaymentUseCase = capturePayPalPaymentUseCase;
    }
    
    // ==================== USER ENDPOINTS ====================
    
    @Operation(
            summary = "Place a new order",
            description = "Creates a new order from the user's cart. Shipping address can be provided or user's saved profile address will be used."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or empty cart"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        
        Long userId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));
        
        PlaceOrderCommand command;
        
        if (request.shouldUseProfileAddress()) {
            // Use saved profile address
                        command = PlaceOrderCommand.withProfileAddress(
                                        userId,
                                        request.paymentMethod(),
                                        request.deliveryNotes()
                        );
        } else {
            // Use provided shipping address
            ShippingAddressRequest addr = request.shippingAddress();
            String deliveryNotes = request.deliveryNotes() != null 
                    ? request.deliveryNotes() 
                    : addr.deliveryNotes();
            
            command = PlaceOrderCommand.withAddress(
                    userId,
                    addr.fullName(),
                    addr.street(),
                    addr.addressLine2(),
                    addr.city(),
                    addr.state(),
                    addr.postalCode(),
                    addr.country(),
                    deliveryNotes,
                    request.paymentMethod()
            );
        }
        
        Order order = placeOrderUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderResponse.fromDomain(order));
    }
    
    @Operation(
            summary = "Get my orders",
            description = "Retrieves all orders for the current authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        Long userId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));
        
        List<Order> orders = getOrderUseCase.getOrders(GetOrderQuery.forUser(userId));
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @Operation(
            summary = "Get order by ID",
            description = "Retrieves a specific order by its ID. Only the order owner can view."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        Long userId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));
        Order order = assertOrderOwnership(orderId, userId);
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }
    
    @Operation(
            summary = "Cancel an order",
            description = "Cancels an order if the status allows. Users can only cancel their own orders."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @RequestBody(required = false) CancelOrderRequest request) {
        Long userId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));
        assertOrderOwnership(orderId, userId);
        
        String reason = request != null ? request.reason() : null;
        Order order = updateOrderStatusUseCase.execute(UpdateOrderStatusCommand.cancel(orderId, reason));
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }

    @Operation(
            summary = "Initiate PayPal payment",
            description = "Creates a PayPal checkout order and returns approval URL for the authenticated user's order"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PayPal payment initiated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment state"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{orderId}/payments/paypal/initiate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PayPalPaymentInitiationResponse> initiatePayPalPayment(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        Long userId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));
        assertOrderOwnership(orderId, userId);

        PayPalPaymentInitiationResponse response = PayPalPaymentInitiationResponse.fromResult(
                initiatePayPalPaymentUseCase.execute(InitiatePayPalPaymentCommand.of(orderId))
        );
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Capture PayPal payment",
            description = "Captures a previously initiated PayPal payment for the authenticated user's order"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PayPal payment captured successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payment capture failed"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{orderId}/payments/paypal/capture")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> capturePayPalPayment(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @RequestBody(required = false) CapturePayPalPaymentRequest request) {
        Long userId = SecurityContextUtil.getCurrentUserId()
                .orElseThrow(() -> new SecurityException("No authenticated user"));
        assertOrderOwnership(orderId, userId);

        String providerOrderId = request != null ? request.providerOrderId() : null;
        Order order = capturePayPalPaymentUseCase.execute(
                CapturePayPalPaymentCommand.of(orderId, providerOrderId)
        );
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }

    private Order assertOrderOwnership(Long orderId, Long currentUserId) {
        Order order = getOrderUseCase.getOrder(GetOrderQuery.forOrder(orderId));
        if (!order.getUserId().value().equals(currentUserId)) {
            throw new AccessDeniedException("You can only access your own orders");
        }
        return order;
    }
}
