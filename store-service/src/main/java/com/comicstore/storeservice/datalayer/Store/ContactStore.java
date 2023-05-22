package com.comicstore.storeservice.datalayer.Store;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Embeddable
public class ContactStore {
    private String email;
    private String phoneNumber;

    @SuppressWarnings("unused")
    public ContactStore() {
    }

    public ContactStore(@NotNull String email, @NotNull String phoneNumber) {
        Objects.requireNonNull(this.email = email);
        Objects.requireNonNull(this.phoneNumber = phoneNumber);
    }
    public @NotNull String getEmail() {
        return email;
    }
    public @NotNull String getPhoneNumber() {
            return phoneNumber;
    }

   }
