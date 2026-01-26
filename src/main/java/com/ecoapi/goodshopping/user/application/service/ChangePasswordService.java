package com.ecoapi.goodshopping.user.application.service;

import com.ecoapi.goodshopping.user.application.port.in.ChangePasswordUseCase;
import com.ecoapi.goodshopping.user.application.port.out.PasswordEncoderPort;
import com.ecoapi.goodshopping.user.application.port.out.UserEventPublisherPort;
import com.ecoapi.goodshopping.user.application.port.out.UserRepositoryPort;
import com.ecoapi.goodshopping.user.application.service.dto.ChangePasswordCommand;
import com.ecoapi.goodshopping.user.domain.events.PasswordChangedEvent;
import com.ecoapi.goodshopping.user.domain.exception.InvalidCredentialsException;
import com.ecoapi.goodshopping.user.domain.exception.UserNotFoundException;
import com.ecoapi.goodshopping.user.domain.model.User;
import com.ecoapi.goodshopping.common.domain.valueobjects.UserId;

/**
 * Application Service for Changing Password
 * Single Responsibility: Handle password change operations
 */
public class ChangePasswordService implements ChangePasswordUseCase {
    
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final UserEventPublisherPort eventPublisher;
    
    public ChangePasswordService(
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            UserEventPublisherPort eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
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
