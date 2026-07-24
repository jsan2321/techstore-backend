package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.application.port.in.ListAddressesUseCase;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.ListAddressesQuery;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.SavedAddress;
import com.ecoapi.techstore.user.domain.model.User;

import java.util.List;

/**
 * Application service for listing saved addresses.
 */
public class ListAddressesService implements ListAddressesUseCase {

    private final UserRepositoryPort userRepository;

    public ListAddressesService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<SavedAddress> execute(ListAddressesQuery query) {
        User user = userRepository.findById(UserId.of(query.userId()))
                .orElseThrow(() -> UserNotFoundException.byId(query.userId()));

        return user.getAddressBook();
    }
}
