package com.ecoapi.techstore.order.infrastructure.adapter.input.rest;

import com.ecoapi.techstore.common.application.dto.PagedResult;
import com.ecoapi.techstore.order.application.port.in.GetOrderUseCase;
import com.ecoapi.techstore.order.application.port.in.SearchOrderUseCase;
import com.ecoapi.techstore.order.application.port.in.UpdateOrderStatusUseCase;
import com.ecoapi.techstore.order.application.port.out.UserInfoPort;
import com.ecoapi.techstore.order.application.port.out.dto.UserSummaryData;
import com.ecoapi.techstore.order.application.service.dto.GetOrderQuery;
import com.ecoapi.techstore.order.application.service.dto.SearchOrderCriteria;
import com.ecoapi.techstore.order.application.service.dto.UpdateOrderStatusCommand;
import com.ecoapi.techstore.order.domain.model.Order;
import com.ecoapi.techstore.order.domain.model.OrderStatus;
import com.ecoapi.techstore.order.infrastructure.adapter.input.rest.response.AdminOrderResponse;
import com.ecoapi.techstore.order.infrastructure.adapter.input.rest.response.AdminPagedOrderResponse;
import com.ecoapi.techstore.order.infrastructure.adapter.input.rest.response.OrderResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Admin Order operations (Input Adapter)
 * Admin-only endpoints for managing orders across all users
 */
@RestController
@RequestMapping("${api.prefix}/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Orders", description = "Order management endpoints for administrators")
@SecurityRequirement(name = "bearerAuth")
public class AdminOrderController {
    
    private final GetOrderUseCase getOrderUseCase;
    private final SearchOrderUseCase searchOrderUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;
        private final UserInfoPort userInfoPort;
    
    public AdminOrderController(GetOrderUseCase getOrderUseCase,
                                SearchOrderUseCase searchOrderUseCase,
                                                                UpdateOrderStatusUseCase updateOrderStatusUseCase,
                                                                UserInfoPort userInfoPort) {
        this.getOrderUseCase = getOrderUseCase;
        this.searchOrderUseCase = searchOrderUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
                this.userInfoPort = userInfoPort;
    }
    
    // ==================== READ OPERATIONS ====================
    
    @Operation(
            summary = "Search orders",
            description = "Search and filter orders with pagination. Supports filtering by status, date range, amount range, and user ID."
    )
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
            content = @Content(schema = @Schema(implementation = AdminPagedOrderResponse.class)))
    @GetMapping
    public ResponseEntity<AdminPagedOrderResponse> searchOrders(
            @Parameter(description = "Filter by user ID") 
            @RequestParam(required = false) Long userId,
            
            @Parameter(description = "Filter by order status") 
            @RequestParam(required = false) OrderStatus status,
            
            @Parameter(description = "Filter by start date (ISO format: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "Filter by end date (ISO format: yyyy-MM-ddTHH:mm:ss)") 
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            
            @Parameter(description = "Filter by minimum amount") 
            @RequestParam(required = false) BigDecimal minAmount,
            
            @Parameter(description = "Filter by maximum amount") 
            @RequestParam(required = false) BigDecimal maxAmount,
            
            @Parameter(description = "Page number (0-based)") 
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Sort by field (e.g., orderDate, totalAmount, status)") 
            @RequestParam(defaultValue = "orderDate") String sortBy,
            
            @Parameter(description = "Sort direction (asc or desc)") 
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        SearchOrderCriteria criteria = SearchOrderCriteria.builder()
                .userId(userId)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .minAmount(minAmount)
                .maxAmount(maxAmount)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        PagedResult<Order> result = searchOrderUseCase.search(criteria);
        List<AdminOrderResponse> content = result.content().stream()
                .map(this::toAdminOrderResponse)
                .toList();

        return ResponseEntity.ok(AdminPagedOrderResponse.fromPagedResult(result, content));
    }
    
    @Operation(
            summary = "Get order by ID",
            description = "Retrieves a specific order by its ID (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found",
                    content = @Content(schema = @Schema(implementation = AdminOrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<AdminOrderResponse> getOrderById(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        Order order = getOrderUseCase.getOrder(GetOrderQuery.forOrder(orderId));
        return ResponseEntity.ok(toAdminOrderResponse(order));
    }

    private AdminOrderResponse toAdminOrderResponse(Order order) {
        UserSummaryData userSummaryData = userInfoPort.getUserSummary(order.getUserId().value())
                .orElse(null);
        return AdminOrderResponse.fromDomain(order, userSummaryData);
    }
    
    // ==================== STATE TRANSITION OPERATIONS ====================
    // Using POST for state transitions as per REST best practices
    
    @Operation(
            summary = "Process an order",
            description = "Moves an order to PROCESSING status - typically used when order fulfillment begins (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order processed successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{orderId}/process")
    public ResponseEntity<OrderResponse> processOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        Order order = updateOrderStatusUseCase.execute(UpdateOrderStatusCommand.process(orderId));
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }
    
    @Operation(
            summary = "Ship an order",
            description = "Moves an order to SHIPPED status - marks order as shipped (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order shipped successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{orderId}/ship")
    public ResponseEntity<OrderResponse> shipOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        Order order = updateOrderStatusUseCase.execute(UpdateOrderStatusCommand.ship(orderId));
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }
    
    @Operation(
            summary = "Deliver an order",
            description = "Moves an order to DELIVERED status - marks order as delivered (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order delivered successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid status transition"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{orderId}/deliver")
    public ResponseEntity<OrderResponse> deliverOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        Order order = updateOrderStatusUseCase.execute(UpdateOrderStatusCommand.deliver(orderId));
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }
    
    @Operation(
            summary = "Cancel an order",
            description = "Cancels an order with an optional reason - Admin can cancel any order regardless of owner (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Parameter(description = "Cancellation reason") @RequestParam(required = false) String reason) {
        Order order = updateOrderStatusUseCase.execute(UpdateOrderStatusCommand.cancel(orderId, reason));
        return ResponseEntity.ok(OrderResponse.fromDomain(order));
    }
}
