package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.ListAddressesQuery;
import com.ecoapi.techstore.user.domain.model.SavedAddress;

import java.util.List;

/**
 * Input port for listing saved addresses for the authenticated user.
 */
public interface ListAddressesUseCase {

    List<SavedAddress> execute(ListAddressesQuery query);
}
