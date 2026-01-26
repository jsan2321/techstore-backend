package com.ecoapi.goodshopping.user.infrastructure.adapter.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class AddressEmbeddable {
    @Column(name = "address_street")
    private String street;
    
    @Column(name = "address_city")
    private String city;
    
    @Column(name = "address_zipcode")
    private String zipCode;
}