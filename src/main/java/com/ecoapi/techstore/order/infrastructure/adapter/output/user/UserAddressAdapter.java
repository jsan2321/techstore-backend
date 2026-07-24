package com.ecoapi.techstore.order.infrastructure.adapter.output.user;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.order.application.port.out.UserAddressPort;
import com.ecoapi.techstore.order.application.port.out.dto.UserAddressData;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.domain.model.SavedAddress;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.user.domain.valueobjects.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Infrastructure adapter for accessing user address data.
 * Implements the UserAddressPort by delegating to the User context's repository.
 *
 * This adapter translates from the User context's AddressBook
 * to the Order context's UserAddressData DTO (Anti-Corruption Layer).
 */
public class UserAddressAdapter implements UserAddressPort {

    private static final Logger logger = LoggerFactory.getLogger(UserAddressAdapter.class);

    private final UserRepositoryPort userRepository;

    public UserAddressAdapter(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserAddressData> getUserAddress(Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(UserId.of(userId));

            if (userOpt.isEmpty()) {
                logger.debug("User not found with id: {}", userId);
                return Optional.empty();
            }

            User user = userOpt.get();

            // Get the default shipping address from the user's address book
            Optional<SavedAddress> savedAddressOpt = user.getDefaultShippingAddress();

            if (savedAddressOpt.isEmpty()) {
                logger.debug("User {} has no saved shipping address", userId);
                return Optional.empty();
            }

            SavedAddress savedAddress = savedAddressOpt.get();
            Address address = savedAddress.getAddress();

            // Translate from User context's Address to Order context's UserAddressData
            UserAddressData addressData = new UserAddressData(
                    savedAddress.getRecipientName(),
                    address.street(),
                    address.addressLine2(),
                    address.city(),
                    address.state(),
                    address.zipCode(),
                    address.country()
            );

            return Optional.of(addressData);

        } catch (Exception e) {
            logger.error("Error fetching address for user {}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean hasAddress(Long userId) {
        return getUserAddress(userId)
                .map(UserAddressData::isComplete)
                .orElse(false);
    }
}
