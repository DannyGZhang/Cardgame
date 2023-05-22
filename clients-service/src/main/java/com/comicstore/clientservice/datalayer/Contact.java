package com.comicstore.clientservice.datalayer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Embeddable
public class Contact {

    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String phoneNumber;

    @SuppressWarnings("unused")
    public Contact() {
    }

//    public Contact(String email, String phoneNumber) {
//        Objects.requireNonNull(this.email = email);
//        Objects.requireNonNull(this.phoneNumber = phoneNumber);
//    }
    public @NotNull String getEmail() {
        return email;
    }
    public @NotNull String getPhoneNumber() {
            return phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
