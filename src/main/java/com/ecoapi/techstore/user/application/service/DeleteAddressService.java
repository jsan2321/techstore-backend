package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.application.port.in.DeleteAddressUseCase;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.DeleteAddressCommand;
import com.ecoapi.techstore.user.domain.exception.AddressNotFoundException;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.user.domain.valueobjects.AddressId;

/**
 * Application service for deleting a saved address.
 */
public class DeleteAddressService implements DeleteAddressUseCase {

    private final UserRepositoryPort userRepository;

    public DeleteAddressService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(DeleteAddressCommand command) {
        User user = userRepository.findById(UserId.of(command.userId()))
                .orElseThrow(() -> UserNotFoundException.byId(command.userId()));

        AddressId addressId = AddressId.of(command.addressId());
        if (user.findAddressById(addressId).isEmpty()) {
            throw AddressNotFoundException.byId(command.addressId());
        }

        user.removeAddress(addressId);
        userRepository.save(user);
    }
}
