package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.CreateAddressCommand;
import com.ecoapi.techstore.user.domain.model.SavedAddress;

/**
 * Input port for creating a saved address for the authenticated user.
 */
public interface CreateAddressUseCase {

    SavedAddress execute(CreateAddressCommand command);
}
