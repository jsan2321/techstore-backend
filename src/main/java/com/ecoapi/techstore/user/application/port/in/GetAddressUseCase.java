package com.ecoapi.techstore.user.application.port.in;

import com.ecoapi.techstore.user.application.service.dto.GetAddressQuery;
import com.ecoapi.techstore.user.domain.model.SavedAddress;

/**
 * Input port for retrieving one address by id for the authenticated user.
 */
public interface GetAddressUseCase {

    SavedAddress execute(GetAddressQuery query);
}
