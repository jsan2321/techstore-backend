package com.ecoapi.techstore.order.application.service.dto;

/**
 * Command for placing a new order.
 * Contains all necessary information to create an order from a user's cart.
 * 
 * The shipping address is optional - if not provided, the user's saved
 * profile address will be used (if available).
 * 
 * Business defaults are NOT applied here - the Use Case is responsible for defaults.
 * This command represents exactly what was requested.
 */
public record PlaceOrderCommand(
    Long userId,
    ShippingAddressData shippingAddress,  // Can be null - will use user's saved address
    String paymentMethod,
    boolean useProfileAddress,  // Flag to explicitly use profile address
    String deliveryNotes        // Optional notes, also supported with profile address
) {
    public PlaceOrderCommand {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("Payment method is required");
        }
        // Note: shippingAddress can be null - service will resolve from profile if needed
    }
    
    /**
     * Factory method for placing order with explicit shipping address
     */
    public static PlaceOrderCommand withAddress(
            Long userId,
            String fullName,
            String street,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String country,
            String deliveryNotes,
            String paymentMethod) {

        ShippingAddressData shippingAddress = new ShippingAddressData(
                fullName, street, addressLine2, city, state, postalCode, country, deliveryNotes
        );
        return new PlaceOrderCommand(userId, shippingAddress, paymentMethod, false, deliveryNotes);
    }

    /**
     * Factory method for placing order using user's saved profile address
     */
    public static PlaceOrderCommand withProfileAddress(Long userId, String paymentMethod) {
        return new PlaceOrderCommand(userId, null, paymentMethod, true, null);
    }

    /**
     * Factory method with optional delivery notes when using profile address
     */
    public static PlaceOrderCommand withProfileAddress(Long userId, String paymentMethod, String deliveryNotes) {
        // When using profile address, we still allow adding delivery notes
        return new PlaceOrderCommand(userId, null, paymentMethod, true, deliveryNotes);
    }

    /**
     * @deprecated Use withAddress() or withProfileAddress() factory methods
     */
    @Deprecated
    public static PlaceOrderCommand of(
            Long userId,
            String fullName,
            String street,
            String apartment,
            String city,
            String state,
            String postalCode,
            String country,
            String deliveryNotes,
            String paymentMethod) {
        return withAddress(userId, fullName, street, apartment, city, state, postalCode,
                          country, deliveryNotes, paymentMethod);
    }
    
    /**
     * Check if this command has an explicit shipping address
     */
    public boolean hasExplicitAddress() {
        return shippingAddress != null;
    }
}
