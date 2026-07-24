package com.ecoapi.techstore.user.domain.model;

import com.ecoapi.techstore.user.domain.valueobjects.Address;
import com.ecoapi.techstore.user.domain.valueobjects.AddressId;
import com.ecoapi.techstore.user.domain.valueobjects.AddressType;

import java.util.Objects;

/**
 * Entity representing a saved address in the user's address book.
 *
 * Each SavedAddress has its own identity (AddressId) and belongs to a User.
 * Users can label their addresses (e.g., "Home", "Work", "Office") and
 * designate them for different purposes (shipping, billing, or both).
 *
 * One address can be marked as default for faster checkout.
 */
public class SavedAddress {

    private AddressId id;
    private String label;
    private String recipientName;
    private Address address;
    private AddressType type;
    private boolean isDefault;

    // Private constructor - use factory methods
    private SavedAddress(String label, String recipientName, Address address,
                         AddressType type, boolean isDefault) {
        validateLabel(label);
        validateRecipientName(recipientName);
        validateAddress(address);
        validateType(type);

        this.label = label.trim();
        this.recipientName = recipientName.trim();
        this.address = address;
        this.type = type;
        this.isDefault = isDefault;
    }

    // Private constructor for reconstitution
    private SavedAddress(AddressId id, String label, String recipientName,
                         Address address, AddressType type, boolean isDefault) {
        this.id = id;
        this.label = label;
        this.recipientName = recipientName;
        this.address = address;
        this.type = type;
        this.isDefault = isDefault;
    }

    // Factory Methods

    /**
     * Factory method for creating a new saved address
     */
    public static SavedAddress create(String label, String recipientName, Address address,
                                      AddressType type, boolean isDefault) {
        return new SavedAddress(label, recipientName, address, type, isDefault);
    }

    /**
     * Factory method for reconstituting from persistence
     */
    public static SavedAddress reconstitute(AddressId id, String label, String recipientName,
                                            Address address, AddressType type, boolean isDefault) {
        return new SavedAddress(id, label, recipientName, address, type, isDefault);
    }

    // Business logic methods

    /**
     * Updates the address details
     */
    public void update(String label, String recipientName, Address address, AddressType type) {
        validateLabel(label);
        validateRecipientName(recipientName);
        validateAddress(address);
        validateType(type);

        this.label = label.trim();
        this.recipientName = recipientName.trim();
        this.address = address;
        this.type = type;
    }

    /**
     * Marks this address as the default
     */
    public void markAsDefault() {
        this.isDefault = true;
    }

    /**
     * Removes the default status from this address
     */
    public void unmarkAsDefault() {
        this.isDefault = false;
    }

    /**
     * Checks if this address can be used for shipping
     */
    public boolean canBeUsedForShipping() {
        return type == AddressType.SHIPPING || type == AddressType.BOTH;
    }

    /**
     * Checks if this address can be used for billing
     */
    public boolean canBeUsedForBilling() {
        return type == AddressType.BILLING || type == AddressType.BOTH;
    }

    // Validation methods

    private void validateLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Address label cannot be null or empty");
        }
        if (label.length() > 50) {
            throw new IllegalArgumentException("Address label cannot exceed 50 characters");
        }
    }

    private void validateRecipientName(String recipientName) {
        if (recipientName == null || recipientName.isBlank()) {
            throw new IllegalArgumentException("Recipient name cannot be null or empty");
        }
        if (recipientName.length() > 100) {
            throw new IllegalArgumentException("Recipient name cannot exceed 100 characters");
        }
    }

    private void validateAddress(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
    }

    private void validateType(AddressType type) {
        if (type == null) {
            throw new IllegalArgumentException("Address type cannot be null");
        }
    }

    // Getters

    public AddressId getId() {
        return id;
    }

    public void setId(AddressId id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public Address getAddress() {
        return address;
    }

    public AddressType getType() {
        return type;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavedAddress that = (SavedAddress) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SavedAddress{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", recipientName='" + recipientName + '\'' +
                ", type=" + type +
                ", isDefault=" + isDefault +
                '}';
    }
}
