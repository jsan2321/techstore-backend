package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.UpdateAddressCommand;
import com.ecoapi.techstore.user.domain.model.SavedAddress;

/**
 * Input port for updating an existing saved address.
 */
public interface UpdateAddressUseCase {

    SavedAddress execute(UpdateAddressCommand command);
}
