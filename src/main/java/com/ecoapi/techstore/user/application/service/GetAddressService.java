package com.ecoapi.techstore.user.application.service;

import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.application.port.in.GetAddressUseCase;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.application.service.dto.GetAddressQuery;
import com.ecoapi.techstore.user.domain.exception.AddressNotFoundException;
import com.ecoapi.techstore.user.domain.exception.UserNotFoundException;
import com.ecoapi.techstore.user.domain.model.SavedAddress;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.user.domain.valueobjects.AddressId;

/**
 * Application service for retrieving one saved address.
 */
public class GetAddressService implements GetAddressUseCase {

    private final UserRepositoryPort userRepository;

    public GetAddressService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public SavedAddress execute(GetAddressQuery query) {
        User user = userRepository.findById(UserId.of(query.userId()))
                .orElseThrow(() -> UserNotFoundException.byId(query.userId()));

        return user.findAddressById(AddressId.of(query.addressId()))
                .orElseThrow(() -> AddressNotFoundException.byId(query.addressId()));
    }
}
