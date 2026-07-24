package com.ecoapi.techstore.user.domain.exception;

import com.ecoapi.techstore.common.domain.exceptions.NotFoundException;

/**
 * Exception thrown when an address is not found for the current user.
 */
public class AddressNotFoundException extends NotFoundException {

    public AddressNotFoundException(String message) {
        super(message);
    }

    public static AddressNotFoundException byId(Long id) {
        return new AddressNotFoundException("Address not found with ID: " + id);
    }
}
