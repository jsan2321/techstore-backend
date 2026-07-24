package com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.mapper;

import com.ecoapi.techstore.user.domain.model.SavedAddress;
import com.ecoapi.techstore.user.domain.valueobjects.Address;
import com.ecoapi.techstore.user.domain.valueobjects.AddressId;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.SavedAddressEntity;
import com.ecoapi.techstore.user.infrastructure.adapter.output.persistence.entity.UserEntity;

/**
 * Mapper to convert between SavedAddress Domain Model and SavedAddressEntity JPA Entity
 */
public class SavedAddressPersistenceMapper {

    /**
     * Convert Domain SavedAddress to JPA SavedAddressEntity
     */
    public SavedAddressEntity toEntity(SavedAddress savedAddress, UserEntity userEntity) {
        SavedAddressEntity entity = new SavedAddressEntity();

        if (savedAddress.getId() != null) {
            entity.setId(savedAddress.getId().value());
        }

        entity.setUser(userEntity);
        entity.setLabel(savedAddress.getLabel());
        entity.setRecipientName(savedAddress.getRecipientName());

        Address address = savedAddress.getAddress();
        entity.setStreet(address.street());
        entity.setAddressLine2(address.addressLine2());
        entity.setCity(address.city());
        entity.setState(address.state());
        entity.setZipCode(address.zipCode());
        entity.setCountry(address.country());

        entity.setType(savedAddress.getType());
        entity.setDefault(savedAddress.isDefault());

        return entity;
    }

    /**
     * Convert JPA SavedAddressEntity to Domain SavedAddress
     */
    public SavedAddress toDomain(SavedAddressEntity entity) {
        AddressId addressId = entity.getId() != null ? AddressId.of(entity.getId()) : null;

        Address address = Address.of(
                entity.getStreet(),
                entity.getAddressLine2(),
                entity.getCity(),
                entity.getState(),
                entity.getZipCode(),
                entity.getCountry()
        );

        return SavedAddress.reconstitute(
                addressId,
                entity.getLabel(),
                entity.getRecipientName(),
                address,
                entity.getType(),
                entity.isDefault()
        );
    }
}
