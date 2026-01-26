package com.ecoapi.goodshopping.user.application.service;

import com.ecoapi.goodshopping.common.domain.valueobjects.Email;
import com.ecoapi.goodshopping.user.application.port.in.RegisterUserUseCase;
import com.ecoapi.goodshopping.user.application.port.out.PasswordEncoderPort;
import com.ecoapi.goodshopping.user.application.port.out.RoleRepositoryPort;
import com.ecoapi.goodshopping.user.application.port.out.UserEventPublisherPort;
import com.ecoapi.goodshopping.user.application.port.out.UserRepositoryPort;
import com.ecoapi.goodshopping.user.application.service.dto.RegisterCommand;
import com.ecoapi.goodshopping.user.domain.events.UserRegisteredEvent;
import com.ecoapi.goodshopping.user.domain.exception.EmailAlreadyExistsException;
import com.ecoapi.goodshopping.user.domain.model.Role;
import com.ecoapi.goodshopping.user.domain.model.User;

/**
 * Application Service for User Registration
 * Single Responsibility: Handle user registration business logic
 */
public class RegisterUserService implements RegisterUserUseCase {
    
    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final UserEventPublisherPort eventPublisher;
    
    public RegisterUserService(
            UserRepositoryPort userRepository,
            RoleRepositoryPort roleRepository,
            PasswordEncoderPort passwordEncoder,
            UserEventPublisherPort eventPublisher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public User execute(RegisterCommand command) {
        // Convert to Email value object
        Email email = new Email(command.email());
        
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(command.email());
        }
        
        // Encode password
        String passwordHash = passwordEncoder.encode(command.password());
        
        // Create domain entity using factory method
        User user = User.register(
            command.firstName(),
            command.lastName(),
            email,
            passwordHash
        );
        
        // Assign default role (USER)
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        user.addRole(userRole);
        
        // Save to database
        User savedUser = userRepository.save(user);
        
        // Publish domain event
        UserRegisteredEvent event = new UserRegisteredEvent(
            savedUser.getId(),
            savedUser.getEmail().value(),
            savedUser.getFirstName(),
            savedUser.getLastName()
        );
        eventPublisher.publish(event);
        
        return savedUser;
    }
}
