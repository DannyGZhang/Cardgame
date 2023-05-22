package com.comicstore.storeservice.datalayer.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Objects;

@Embeddable
public class Address {

    @Column(unique = true)
    private String streetAddress;
    private String city;
    private String province;
    @Column(unique = true)
    private String postalCode;

    @SuppressWarnings("unused")
    public Address() {
    }

    public Address(@NotNull String streetAddress, @NotNull String city, @NotNull String province,  @NotNull String postalCode) {

        Objects.requireNonNull(this.streetAddress = streetAddress);
        Objects.requireNonNull(this.city = city);
        Objects.requireNonNull(this.province = province);
        Objects.requireNonNull(this.postalCode = postalCode);

    }


    public @NotNull String getStreetAddress() {
        return streetAddress;
    }

    public @NotNull String getCity() {
        return city;
    }

    public @NotNull String getProvince() {
        return province;
    }



    public @NotNull String getPostalCode() {
        return postalCode;
    }

  }