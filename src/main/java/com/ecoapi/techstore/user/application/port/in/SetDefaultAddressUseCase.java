package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.SetDefaultAddressCommand;
import com.ecoapi.techstore.user.domain.model.SavedAddress;

/**
 * Input port for marking a saved address as default.
 */
public interface SetDefaultAddressUseCase {

    SavedAddress execute(SetDefaultAddressCommand command);
}
