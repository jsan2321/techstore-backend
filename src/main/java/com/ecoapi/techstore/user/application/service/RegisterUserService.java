package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.Email;
import com.ecoapi.techstore.user.application.port.out.EmailVerificationTokenRepositoryPort;
import com.ecoapi.techstore.user.application.port.in.RegisterUserUseCase;
import com.ecoapi.techstore.user.application.port.out.PasswordEncoderPort;
import com.ecoapi.techstore.user.application.port.out.RoleRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserEmailNotificationPort;
import com.ecoapi.techstore.user.application.port.out.UserEventPublisherPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.RegisterCommand;
import com.ecoapi.techstore.user.domain.events.UserRegisteredEvent;
import com.ecoapi.techstore.user.domain.exception.EmailAlreadyExistsException;
import com.ecoapi.techstore.user.domain.model.EmailVerificationToken;
import com.ecoapi.techstore.user.domain.model.Role;
import com.ecoapi.techstore.user.domain.model.RoleName;
import com.ecoapi.techstore.user.domain.model.User;

/**
 * Application Service for User Registration
 * Single Responsibility: Handle user registration business logic
 */
public class RegisterUserService implements RegisterUserUseCase {
    
    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final UserEventPublisherPort eventPublisher;
    private final EmailVerificationTokenRepositoryPort emailVerificationTokenRepository;
    private final UserEmailNotificationPort userEmailNotificationPort;
    private final int emailVerificationExpiryHours;
    private final String frontendBaseUrl;
    
    public RegisterUserService(
            UserRepositoryPort userRepository,
            RoleRepositoryPort roleRepository,
            PasswordEncoderPort passwordEncoder,
            UserEventPublisherPort eventPublisher,
            EmailVerificationTokenRepositoryPort emailVerificationTokenRepository,
            UserEmailNotificationPort userEmailNotificationPort,
            int emailVerificationExpiryHours,
            String frontendBaseUrl) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userEmailNotificationPort = userEmailNotificationPort;
        this.emailVerificationExpiryHours = emailVerificationExpiryHours;
        this.frontendBaseUrl = frontendBaseUrl;
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
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
        .orElseGet(() -> {
            // If the default role doesn't exist, create it
            Role newRole = new Role(RoleName.ROLE_USER);
            return roleRepository.save(newRole);
        });
            //.orElseThrow(() -> new IllegalStateException("System Error: Default role 'ROLE_USER' not initialized in database."));

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

        EmailVerificationToken verificationToken = EmailVerificationToken.create(
            savedUser.getId(),
            emailVerificationExpiryHours
        );
        emailVerificationTokenRepository.save(verificationToken);

        String confirmationLink = frontendBaseUrl + "/auth/confirm-email?token=" + verificationToken.getToken();
        userEmailNotificationPort.sendEmailConfirmation(
            savedUser.getEmail().value(),
            savedUser.getFirstName(),
            confirmationLink
        );
        
        return savedUser;
    }
}
