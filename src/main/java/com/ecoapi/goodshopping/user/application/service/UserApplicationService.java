package com.ecoapi.goodshopping.user.application.service;

import com.ecoapi.goodshopping.common.domain.valueobjects.Email;
import com.ecoapi.goodshopping.user.application.port.in.*;
import com.ecoapi.goodshopping.user.application.port.out.*;
import com.ecoapi.goodshopping.user.application.service.dto.*;
import com.ecoapi.goodshopping.user.domain.events.PasswordChangedEvent;
import com.ecoapi.goodshopping.user.domain.events.UserRegisteredEvent;
import com.ecoapi.goodshopping.user.domain.exception.EmailAlreadyExistsException;
import com.ecoapi.goodshopping.user.domain.exception.InvalidCredentialsException;
import com.ecoapi.goodshopping.user.domain.exception.UserNotFoundException;
import com.ecoapi.goodshopping.user.domain.model.Role;
import com.ecoapi.goodshopping.user.domain.model.User;
import com.ecoapi.goodshopping.user.domain.model.UserId;

/**
 * Application Service implementing all User Use Cases
 * This is the orchestration layer - coordinates domain logic and infrastructure
 */
public class UserApplicationService implements 
        RegisterUserUseCase,
        LoginUseCase,
        GetUserProfileUseCase,
        UpdateUserUseCase,
        ChangePasswordUseCase {
    
    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;
    private final UserEventPublisherPort eventPublisher;
    
    public UserApplicationService(
            UserRepositoryPort userRepository,
            RoleRepositoryPort roleRepository,
            PasswordEncoderPort passwordEncoder,
            TokenProviderPort tokenProvider,
            UserEventPublisherPort eventPublisher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
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
    
    @Override
    public AuthenticationResult execute(LoginCommand command) {
        // Convert to Email value object
        Email email = new Email(command.email());
        
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        
        // Verify password
        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        
        // Check if user is active
        if (!user.isActive()) {
            throw new InvalidCredentialsException();
        }
        
        // Generate token
        String token = tokenProvider.generateToken(user);
        
        return new AuthenticationResult(user, token);
    }
    
    @Override
    public User execute(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId.value()));
    }
    
    @Override
    public User execute(UpdateUserCommand command) {
        UserId userId = UserId.of(command.userId());
        
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(command.userId()));
        
        // Update profile using domain method
        user.updateProfile(command.firstName(), command.lastName());
        
        // Save changes
        return userRepository.save(user);
    }
    
    @Override
    public void execute(ChangePasswordCommand command) {
        UserId userId = UserId.of(command.userId());
        
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(command.userId()));
        
        // Verify current password
        if (!passwordEncoder.matches(command.currentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        
        // Encode new password
        String newPasswordHash = passwordEncoder.encode(command.newPassword());
        
        // Change password using domain method
        user.changePassword(newPasswordHash);
        
        // Save changes
        userRepository.save(user);
        
        // Publish event
        PasswordChangedEvent event = new PasswordChangedEvent(user.getId());
        eventPublisher.publish(event);
    }
}
