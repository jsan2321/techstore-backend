package com.ecoapi.techstore.order.infrastructure.config;

import com.ecoapi.techstore.cart.application.port.in.ClearCartUseCase;
import com.ecoapi.techstore.cart.application.port.in.GetCartUseCase;
import com.ecoapi.techstore.cart.application.port.out.ProductAccessPort;
import com.ecoapi.techstore.order.application.port.in.CapturePayPalPaymentUseCase;
import com.ecoapi.techstore.order.application.port.in.GetOrderUseCase;
import com.ecoapi.techstore.order.application.port.in.InitiatePayPalPaymentUseCase;
import com.ecoapi.techstore.order.application.port.in.PlaceOrderUseCase;
import com.ecoapi.techstore.order.application.port.in.SearchOrderUseCase;
import com.ecoapi.techstore.order.application.port.in.UpdateOrderStatusUseCase;
import com.ecoapi.techstore.order.application.port.out.CartAccessPort;
import com.ecoapi.techstore.order.application.port.out.OrderEventPublisherPort;
import com.ecoapi.techstore.order.application.port.out.OrderRepositoryPort;
import com.ecoapi.techstore.order.application.port.out.PaymentProviderPort;
import com.ecoapi.techstore.order.application.port.out.StockReservationPort;
import com.ecoapi.techstore.order.application.port.out.UserAddressPort;
import com.ecoapi.techstore.order.application.port.out.UserInfoPort;
import com.ecoapi.techstore.order.application.port.out.UserValidationPort;
import com.ecoapi.techstore.order.application.service.CapturePayPalPaymentService;
import com.ecoapi.techstore.order.application.service.GetOrderService;
import com.ecoapi.techstore.order.application.service.InitiatePayPalPaymentService;
import com.ecoapi.techstore.order.application.service.PlaceOrderService;
import com.ecoapi.techstore.order.application.service.SearchOrderService;
import com.ecoapi.techstore.order.application.service.UpdateOrderStatusService;
import com.ecoapi.techstore.order.infrastructure.adapter.TransactionalOrderUseCaseWrapper.TransactionalCapturePayPalPaymentUseCase;
import com.ecoapi.techstore.order.infrastructure.adapter.TransactionalOrderUseCaseWrapper.TransactionalGetOrderUseCase;
import com.ecoapi.techstore.order.infrastructure.adapter.TransactionalOrderUseCaseWrapper.TransactionalInitiatePayPalPaymentUseCase;
import com.ecoapi.techstore.order.infrastructure.adapter.TransactionalOrderUseCaseWrapper.TransactionalPlaceOrderUseCase;
import com.ecoapi.techstore.order.infrastructure.adapter.TransactionalOrderUseCaseWrapper.TransactionalSearchOrderUseCase;
import com.ecoapi.techstore.order.infrastructure.adapter.TransactionalOrderUseCaseWrapper.TransactionalUpdateOrderStatusUseCase;
import com.ecoapi.techstore.order.infrastructure.adapter.output.CartAccessAdapter;
import com.ecoapi.techstore.order.infrastructure.adapter.output.events.TransactionalOrderEventPublisherAdapter;
import com.ecoapi.techstore.order.infrastructure.adapter.output.payment.PayPalPaymentProviderAdapter;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.OrderRepositoryAdapter;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.mapper.OrderPersistenceMapper;
import com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.repository.JpaOrderRepository;
import com.ecoapi.techstore.order.infrastructure.adapter.output.product.StockReservationAdapter;
import com.ecoapi.techstore.order.infrastructure.adapter.output.user.UserAddressAdapter;
import com.ecoapi.techstore.order.infrastructure.adapter.output.user.UserInfoAdapter;
import com.ecoapi.techstore.order.infrastructure.adapter.output.user.UserValidationAdapter;
import com.ecoapi.techstore.product.application.port.out.ProductRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

/**
 * Spring Configuration for the Order Bounded Context
 * Wires all ports and adapters following Hexagonal Architecture principles
 * 
 * Transactional boundaries are managed at the infrastructure layer using wrapper classes,
 * keeping the application layer framework-agnostic.
 * 
 * Inter-context communication is done through output ports:
 * - CartAccessPort: For accessing Cart context (uses Cart's input ports)
 * - UserValidationPort: For validating users (uses User's repository)
 * - UserAddressPort: For fetching user's saved address (ACL pattern)
 * - StockReservationPort: For managing stock (uses Product's repository)
 */
@Configuration
public class OrderConfiguration {
    
    // ===== Infrastructure Mappers =====
    
    @Bean
    public OrderPersistenceMapper orderPersistenceMapper() {
        return new OrderPersistenceMapper();
    }

    // ===== Output Port Adapters =====
    
    @Bean
    public OrderRepositoryPort orderRepositoryPort(JpaOrderRepository jpaOrderRepository,
                                                  OrderPersistenceMapper mapper) {
        return new OrderRepositoryAdapter(jpaOrderRepository, mapper);
    }
    
    @Bean
    public OrderEventPublisherPort orderEventPublisherPort(ApplicationEventPublisher eventPublisher) {
        // Use transactional-aware publisher to ensure events are only published after commit
        return new TransactionalOrderEventPublisherAdapter(eventPublisher);
    }
    
    @Bean
    public UserValidationPort userValidationPort(UserRepositoryPort userRepository) {
        return new UserValidationAdapter(userRepository);
    }
    
    @Bean
    public UserAddressPort userAddressPort(UserRepositoryPort userRepository) {
        return new UserAddressAdapter(userRepository);
    }

    @Bean
    public UserInfoPort userInfoPort(UserRepositoryPort userRepository) {
        return new UserInfoAdapter(userRepository);
    }
    
    @Bean
    public StockReservationPort stockReservationPort(ProductRepositoryPort productRepository) {
        return new StockReservationAdapter(productRepository);
    }
    
    @Bean
    public CartAccessPort cartAccessPort(GetCartUseCase getCartUseCase, 
                                         ClearCartUseCase clearCartUseCase,
                                         ProductAccessPort productAccessPort) {
        return new CartAccessAdapter(getCartUseCase, clearCartUseCase, productAccessPort);
    }

    @Bean
    public PaymentProviderPort paymentProviderPort(
            ObjectMapper objectMapper,
            @Value("${paypal.client-id}") String clientId,
            @Value("${paypal.secret}") String secret,
            @Value("${paypal.api-base-url}") String apiBaseUrl,
            @Value("${paypal.return-url}") String returnUrl,
            @Value("${paypal.cancel-url}") String cancelUrl) {
        return new PayPalPaymentProviderAdapter(objectMapper, clientId, secret, apiBaseUrl, returnUrl, cancelUrl);
    }
    
    // ===== Application Services (Use Case Implementations) =====
    // Wrapped with transactional decorators to ensure proper transaction boundaries
    
    @Bean
    public PlaceOrderUseCase placeOrderUseCase(OrderRepositoryPort orderRepository,
                                               CartAccessPort cartAccessPort,
                                               StockReservationPort stockReservationPort,
                                               UserValidationPort userValidationPort,
                                               UserAddressPort userAddressPort,
                                               OrderEventPublisherPort eventPublisher) {
        // Create the core service
        PlaceOrderService service = new PlaceOrderService(
                orderRepository, 
                cartAccessPort,
                stockReservationPort,
                userValidationPort,
                userAddressPort,
                eventPublisher
        );
        // Wrap with transactional decorator for atomicity
        return new TransactionalPlaceOrderUseCase(service);
    }
    
    @Bean
    public GetOrderUseCase getOrderUseCase(OrderRepositoryPort orderRepository) {
        GetOrderService service = new GetOrderService(orderRepository);
        // Wrap with read-only transactional decorator
        return new TransactionalGetOrderUseCase(service);
    }
    
    @Bean
    public UpdateOrderStatusUseCase updateOrderStatusUseCase(OrderRepositoryPort orderRepository,
                                                             StockReservationPort stockReservationPort,
                                                             OrderEventPublisherPort eventPublisher) {
        UpdateOrderStatusService service = new UpdateOrderStatusService(
                orderRepository, stockReservationPort, eventPublisher);
        // Wrap with transactional decorator for atomicity
        return new TransactionalUpdateOrderStatusUseCase(service);
    }
    
    @Bean
    public SearchOrderUseCase searchOrderUseCase(OrderRepositoryPort orderRepository) {
        SearchOrderService service = new SearchOrderService(orderRepository);
        return new TransactionalSearchOrderUseCase(service);
    }

    @Bean
    public InitiatePayPalPaymentUseCase initiatePayPalPaymentUseCase(OrderRepositoryPort orderRepository,
                                                                      PaymentProviderPort paymentProviderPort) {
        InitiatePayPalPaymentService service = new InitiatePayPalPaymentService(orderRepository, paymentProviderPort);
        return new TransactionalInitiatePayPalPaymentUseCase(service);
    }

    @Bean
    public CapturePayPalPaymentUseCase capturePayPalPaymentUseCase(OrderRepositoryPort orderRepository,
                                                                    PaymentProviderPort paymentProviderPort) {
        CapturePayPalPaymentService service = new CapturePayPalPaymentService(orderRepository, paymentProviderPort);
        return new TransactionalCapturePayPalPaymentUseCase(service);
    }
}
