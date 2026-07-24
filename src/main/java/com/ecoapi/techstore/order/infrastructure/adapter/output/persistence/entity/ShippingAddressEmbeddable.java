package com.ecoapi.techstore.order.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * JPA Embeddable for storing shipping address within OrderEntity.
 * Orders capture the shipping address at the time of purchase as
 * part of the legal contract.
 */
@Embeddable
public class ShippingAddressEmbeddable {

    @Column(name = "shipping_full_name", nullable = false)
    private String fullName;

    @Column(name = "shipping_street", nullable = false)
    private String street;
    
    @Column(name = "shipping_line2")
    private String addressLine2;
    
    @Column(name = "shipping_city", nullable = false)
    private String city;
    
    @Column(name = "shipping_state", nullable = false)
    private String state;
    
    @Column(name = "shipping_postal_code", nullable = false)
    private String postalCode;
    
    @Column(name = "shipping_country", nullable = false, length = 2)
    private String country;
    
    @Column(name = "shipping_delivery_notes", length = 500)
    private String deliveryNotes;
    
    // Default constructor for JPA
    public ShippingAddressEmbeddable() {}

    public ShippingAddressEmbeddable(String fullName, String street, String addressLine2, String city,
                                     String state, String postalCode, String country,
                                     String deliveryNotes) {
        this.fullName = fullName;
        this.street = street;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.deliveryNotes = deliveryNotes;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStreet() {
        return street;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public String getAddressLine2() {
        return addressLine2;
    }
    
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getDeliveryNotes() {
        return deliveryNotes;
    }
    
    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }
}
