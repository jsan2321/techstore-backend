package com.ecoapi.techstore.user.domain.model;

import com.ecoapi.techstore.common.domain.valueobjects.Email;
import com.ecoapi.techstore.common.domain.valueobjects.PhoneNumber;
import com.ecoapi.techstore.common.domain.valueobjects.UserId;
import com.ecoapi.techstore.user.domain.valueobjects.Address;
import com.ecoapi.techstore.user.domain.valueobjects.AddressId;
import com.ecoapi.techstore.user.domain.valueobjects.AddressType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * User Aggregate Root - Pure domain model
 * Contains business logic and invariants
 * No JPA annotations - this is the core business entity
 */
public class User {
    
    private UserId id;
    private String firstName;
    private String lastName;
    private Email email;
    private String passwordHash;
    private PhoneNumber phoneNumber;
    private Set<Role> roles;
    private List<SavedAddress> addressBook;
    private UserStatus status;
    private boolean emailVerified;
    private LocalDateTime accessTokenInvalidBefore;

    // Private constructor - use factory methods
    private User(String firstName, String lastName, Email email, String passwordHash) {
        validateFirstName(firstName);
        validateLastName(lastName);
        validatePasswordHash(passwordHash);

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = new HashSet<>();
        this.addressBook = new ArrayList<>();
        this.status = UserStatus.ACTIVE;
        this.emailVerified = false;
        this.accessTokenInvalidBefore = null;

    }

    // Private constructor for reconstitution - use factory methods
    private User(UserId id, String firstName, String lastName, Email email,
                 String passwordHash, PhoneNumber phoneNumber, Set<Role> roles,
                 List<SavedAddress> addressBook, UserStatus status, boolean emailVerified,
                 LocalDateTime accessTokenInvalidBefore) {
        validateFirstName(firstName);
        validateLastName(lastName);
        validatePasswordHash(passwordHash);

        this.id = id;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.addressBook = addressBook != null ? new ArrayList<>(addressBook) : new ArrayList<>();
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.emailVerified = emailVerified;
        this.accessTokenInvalidBefore = accessTokenInvalidBefore;
    }
    
    // Factory Methods
    
    /**
     * Factory method for user registration
     * Creates a new active user with default roles
     */
    public static User register(String firstName, String lastName, Email email, String passwordHash) {
        return new User(firstName, lastName, email, passwordHash);
    }
    
    /**
     * Factory method for reconstituting user from persistence
     * Used by infrastructure layer to rebuild domain object from database
     */
    public static User reconstitute(UserId id, String firstName, String lastName, Email email,
                                    String passwordHash, PhoneNumber phoneNumber, Set<Role> roles,
                        List<SavedAddress> addressBook, UserStatus status, boolean emailVerified,
                                    LocalDateTime accessTokenInvalidBefore) {
        return new User(id, firstName, lastName, email, passwordHash, phoneNumber, roles, addressBook, status,
            emailVerified, accessTokenInvalidBefore);
    }
    
    // Business logic methods
    
    public void updateProfile(String firstName, String lastName, PhoneNumber phoneNumber) {
        validateFirstName(firstName);
        validateLastName(lastName);
        
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.phoneNumber = phoneNumber;
    }

    public void addRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.roles.add(role);
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.accessTokenInvalidBefore = LocalDateTime.now();
    }

    public void reactivate() {
        this.status = UserStatus.ACTIVE;
    }

    public void confirmEmail() {
        this.emailVerified = true;
    }

    public void changePassword(String newPasswordHash) {
        validatePasswordHash(newPasswordHash);
        this.passwordHash = newPasswordHash;
        this.accessTokenInvalidBefore = LocalDateTime.now();
    }

    public void invalidateAccessTokens() {
        this.accessTokenInvalidBefore = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    // Address Book management methods

    /**
     * Adds a new address to the user's address book
     */
    public void addAddress(SavedAddress address) {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }

        // If this is the first address or marked as default, handle default status
        if (address.isDefault() || addressBook.isEmpty()) {
            clearDefaultAddresses();
            address.markAsDefault();
        }

        addressBook.add(address);
    }

    /**
     * Updates an existing address in the address book
     */
    public void updateAddress(AddressId addressId, String label, String recipientName,
                              Address address, AddressType type) {
        SavedAddress savedAddress = findAddressById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found: " + addressId));

        savedAddress.update(label, recipientName, address, type);
    }

    /**
     * Removes an address from the address book
     */
    public void removeAddress(AddressId addressId) {
        SavedAddress toRemove = findAddressById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found: " + addressId));

        boolean wasDefault = toRemove.isDefault();
        addressBook.remove(toRemove);

        // If we removed the default address, set a new default
        if (wasDefault && !addressBook.isEmpty()) {
            addressBook.get(0).markAsDefault();
        }
    }

    /**
     * Sets an address as the default
     */
    public void setDefaultAddress(AddressId addressId) {
        SavedAddress address = findAddressById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found: " + addressId));

        clearDefaultAddresses();
        address.markAsDefault();
    }

    /**
     * Gets the default address if one exists
     */
    public Optional<SavedAddress> getDefaultAddress() {
        return addressBook.stream()
                .filter(SavedAddress::isDefault)
                .findFirst();
    }

    /**
     * Gets the default shipping address if one exists
     */
    public Optional<SavedAddress> getDefaultShippingAddress() {
        return addressBook.stream()
                .filter(SavedAddress::canBeUsedForShipping)
                .filter(SavedAddress::isDefault)
                .findFirst()
                .or(() -> addressBook.stream()
                        .filter(SavedAddress::canBeUsedForShipping)
                        .findFirst());
    }

    /**
     * Finds an address by its ID
     */
    public Optional<SavedAddress> findAddressById(AddressId addressId) {
        return addressBook.stream()
                .filter(a -> a.getId() != null && a.getId().equals(addressId))
                .findFirst();
    }

    /**
     * Gets all addresses that can be used for shipping
     */
    public List<SavedAddress> getShippingAddresses() {
        return addressBook.stream()
                .filter(SavedAddress::canBeUsedForShipping)
                .toList();
    }

    /**
     * Gets all addresses that can be used for billing
     */
    public List<SavedAddress> getBillingAddresses() {
        return addressBook.stream()
                .filter(SavedAddress::canBeUsedForBilling)
                .toList();
    }

    private void clearDefaultAddresses() {
        addressBook.forEach(SavedAddress::unmarkAsDefault);
    }

    // Validation methods
    
    private void validateFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (firstName.length() > 50) {
            throw new IllegalArgumentException("First name cannot exceed 50 characters");
        }
    }
    
    private void validateLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (lastName.length() > 50) {
            throw new IllegalArgumentException("Last name cannot exceed 50 characters");
        }
    }
    
    private void validatePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or empty");
        }
    }
    
    // Getters
    
    public UserId getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public Email getEmail() {
        return email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }

    public Set<Role> getRoles() {
        return new HashSet<>(roles);
    }
    
    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public List<SavedAddress> getAddressBook() {
        return Collections.unmodifiableList(addressBook);
    }

    public UserStatus getStatus() {
        return status;
    }

    public LocalDateTime getAccessTokenInvalidBefore() {
        return accessTokenInvalidBefore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email=" + email +
                ", status=" + status +
                ", emailVerified=" + emailVerified +
                ", accessTokenInvalidBefore=" + accessTokenInvalidBefore +
                ", roles=" + roles.size() +
                ", addresses=" + addressBook.size() +
                '}';
    }
}
