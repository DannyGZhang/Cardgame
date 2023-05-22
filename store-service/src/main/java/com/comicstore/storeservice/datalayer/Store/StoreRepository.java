package com.comicstore.storeservice.datalayer.Store;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Integer> {
    Store findByStoreIdentifier_StoreId(String storeId);

    Boolean existsByStoreIdentifier_StoreId(String storeId);
}
