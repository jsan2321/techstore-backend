package com.ecoapi.techstore.order.infrastructure.adapter.output.user;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.order.application.port.out.UserInfoPort;
import com.ecoapi.techstore.order.application.port.out.dto.UserSummaryData;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;

import java.util.Optional;

/**
 * Infrastructure adapter for fetching user summary information.
 */
public class UserInfoAdapter implements UserInfoPort {

    private final UserRepositoryPort userRepository;

    public UserInfoAdapter(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserSummaryData> getUserSummary(Long userId) {
        return userRepository.findById(UserId.of(userId))
                .map(user -> new UserSummaryData(
                        user.getId() != null ? user.getId().value() : null,
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail().value(),
                        user.getStatus().name(),
                        user.isEmailVerified()
                ));
    }
}
