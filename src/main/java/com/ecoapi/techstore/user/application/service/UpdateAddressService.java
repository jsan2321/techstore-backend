package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.application.port.in.UpdateAddressUseCase;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.UpdateAddressCommand;
import com.ecoapi.techstore.user.domain.exception.AddressNotFoundException;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.SavedAddress;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.user.domain.valueobjects.Address;
import com.ecoapi.techstore.user.domain.valueobjects.AddressId;

/**
 * Application service for updating an existing saved address.
 */
public class UpdateAddressService implements UpdateAddressUseCase {

    private final UserRepositoryPort userRepository;

    public UpdateAddressService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public SavedAddress execute(UpdateAddressCommand command) {
        User user = userRepository.findById(UserId.of(command.userId()))
                .orElseThrow(() -> UserNotFoundException.byId(command.userId()));

        AddressId addressId = AddressId.of(command.addressId());
        if (user.findAddressById(addressId).isEmpty()) {
            throw AddressNotFoundException.byId(command.addressId());
        }

        Address address = Address.of(
                command.street(),
                command.addressLine2(),
                command.city(),
                command.state(),
                command.zipCode(),
                command.country()
        );

        user.updateAddress(
                addressId,
                command.label(),
                command.recipientName(),
                address,
                command.type()
        );

        User savedUser = userRepository.save(user);
        return savedUser.findAddressById(addressId)
                .orElseThrow(() -> AddressNotFoundException.byId(command.addressId()));
    }
}
