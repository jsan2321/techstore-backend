package com.ecoapi.goodshopping.user.infrastructure.config;

import com.ecoapi.goodshopping.user.application.port.in.*;
import com.ecoapi.goodshopping.user.application.port.out.*;
import com.ecoapi.goodshopping.user.application.service.UserApplicationService;
import com.ecoapi.goodshopping.user.domain.service.UserDomainService;
import com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.mapper.UserPersistenceMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for User Bounded Context
 * This is where we wire everything together
 * Following the Dependency Inversion Principle:
 * - Application layer depends on interfaces (ports)
 * - Infrastructure implements those interfaces
 * - This configuration wires them together
 * 
 * Note: PasswordEncoder bean is defined in SecurityConfig
 */
@Configuration
public class UserConfiguration {
    
    /**
     * Configure the persistence mapper
     */
    @Bean
    public UserPersistenceMapper userPersistenceMapper() {
        return new UserPersistenceMapper();
    }
    
    /**
     * Configure the domain service
     */
    @Bean
    public UserDomainService userDomainService() {
        return new UserDomainService();
    }
    
    /**
     * Configure the Application Service that implements all Use Cases
     * This is the heart of the application - it coordinates everything
     */
    @Bean
    public UserApplicationService userApplicationService(
            UserRepositoryPort userRepositoryPort,
            RoleRepositoryPort roleRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            TokenProviderPort tokenProviderPort,
            UserEventPublisherPort eventPublisherPort) {
        return new UserApplicationService(
                userRepositoryPort,
                roleRepositoryPort,
                passwordEncoderPort,
                tokenProviderPort,
                eventPublisherPort
        );
    }
    
    /**
     * Expose the Application Service as all its Use Case interfaces
     * This allows controllers to depend on specific use cases
     */
    @Bean
    public RegisterUserUseCase registerUserUseCase(UserApplicationService service) {
        return service;
    }
    
    @Bean
    public LoginUseCase loginUseCase(UserApplicationService service) {
        return service;
    }
    
    @Bean
    public GetUserProfileUseCase getUserProfileUseCase(UserApplicationService service) {
        return service;
    }
    
    @Bean
    public UpdateUserUseCase updateUserUseCase(UserApplicationService service) {
        return service;
    }
    
    @Bean
    public ChangePasswordUseCase changePasswordUseCase(UserApplicationService service) {
        return service;
    }
}
