package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.application.port.in.SetDefaultAddressUseCase;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.SetDefaultAddressCommand;
import com.ecoapi.techstore.user.domain.exception.AddressNotFoundException;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.SavedAddress;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.user.domain.valueobjects.AddressId;

/**
 * Application service for setting the default saved address.
 */
public class SetDefaultAddressService implements SetDefaultAddressUseCase {

    private final UserRepositoryPort userRepository;

    public SetDefaultAddressService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public SavedAddress execute(SetDefaultAddressCommand command) {
        User user = userRepository.findById(UserId.of(command.userId()))
                .orElseThrow(() -> UserNotFoundException.byId(command.userId()));

        AddressId addressId = AddressId.of(command.addressId());
        if (user.findAddressById(addressId).isEmpty()) {
            throw AddressNotFoundException.byId(command.addressId());
        }

        user.setDefaultAddress(addressId);
        User savedUser = userRepository.save(user);

        return savedUser.findAddressById(addressId)
                .orElseThrow(() -> AddressNotFoundException.byId(command.addressId()));
    }
}
