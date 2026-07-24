package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.application.port.in.CreateAddressUseCase;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.CreateAddressCommand;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.SavedAddress;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.user.domain.valueobjects.Address;

import java.util.Comparator;

/**
 * Application service for creating a new saved address.
 */
public class CreateAddressService implements CreateAddressUseCase {

    private final UserRepositoryPort userRepository;

    public CreateAddressService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public SavedAddress execute(CreateAddressCommand command) {
        User user = userRepository.findById(UserId.of(command.userId()))
                .orElseThrow(() -> UserNotFoundException.byId(command.userId()));

        Address address = Address.of(
                command.street(),
                command.addressLine2(),
                command.city(),
                command.state(),
                command.zipCode(),
                command.country()
        );

        SavedAddress newAddress = SavedAddress.create(
                command.label(),
                command.recipientName(),
                address,
                command.type(),
                command.isDefault()
        );

        user.addAddress(newAddress);
        User savedUser = userRepository.save(user);

        return savedUser.getAddressBook().stream()
                .filter(savedAddress -> savedAddress.getId() != null)
                .max(Comparator.comparing(savedAddress -> savedAddress.getId().value()))
                .orElseThrow(() -> new IllegalStateException("Created address could not be resolved"));
    }
}
