package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.DeleteAddressCommand;

/**
 * Input port for deleting a saved address.
 */
public interface DeleteAddressUseCase {

    void execute(DeleteAddressCommand command);
}
